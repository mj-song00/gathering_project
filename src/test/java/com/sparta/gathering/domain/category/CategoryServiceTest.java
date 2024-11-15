package com.sparta.gathering.domain.category;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.category.dto.request.CategoryReq;
import com.sparta.gathering.domain.category.dto.response.CategoryRes;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.category.repository.CategoryRepository;
import com.sparta.gathering.domain.category.service.CategoryService;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.IdentityProvider;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

//    private User testUser;
    private CategoryReq categoryReq;
    private AuthenticatedUser authenticatedUser;
    private User user1;
    private User user2;
    private Category category;

    @BeforeEach
    void setUp() {
       user1 = User.builder()
                .id(UUID.randomUUID())
                .email("test1@test.com")
                .password("password123A!")
                .nickName("nickname1")
                .userRole(UserRole.ROLE_ADMIN)
                .identityProvider(IdentityProvider.NONE)
                .profileImage(null)
                .build();
       user2 = User.builder()
             .id(UUID.randomUUID())
             .email("test2@test.com")
             .password("password123A!")
             .nickName("nickname2")
             .userRole(UserRole.ROLE_USER)
             .identityProvider(IdentityProvider.NONE)
             .profileImage(null)
             .build();

        categoryReq = new CategoryReq("운동");
        authenticatedUser = new AuthenticatedUser(user1.getId(), user1.getEmail(), null);
        category = new Category("카테고리 명",user1);


    }

    @Test
    @DisplayName("카테고리 생성 성공")
    void test1() {
        // given
        when(userRepository.findById(authenticatedUser.getUserId())).thenReturn(Optional.of(user1));
        when(categoryRepository.findByCategoryName(categoryReq.getCategoryName())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CategoryRes result = categoryService.createCategory(authenticatedUser, categoryReq);
        // then
        assertNotNull(result);
        assertEquals(categoryReq.getCategoryName(), result.getCategoryName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 중복있음 예외처리")
    void test2() {
        // given
        when(userRepository.findById(authenticatedUser.getUserId())).thenReturn(Optional.of(user1));
        when(categoryRepository.findByCategoryName(categoryReq.getCategoryName())).thenReturn(Optional.of(new Category()));

        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                categoryService.createCategory(authenticatedUser, categoryReq)
        );
        // then
        assertEquals(ExceptionEnum.ALREADY_HAVE_CATEGORY, exception.getExceptionEnum());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 생성 유저 예외처리")
    void test3() {
        // given
        when(userRepository.findById(authenticatedUser.getUserId())).thenReturn(Optional.empty());
        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                categoryService.createCategory(authenticatedUser, categoryReq)
        );
        // then
        assertEquals(ExceptionEnum.USER_NOT_FOUND, exception.getExceptionEnum());
        verify(categoryRepository, never()).save(any(Category.class));
    }


    @Test
    @DisplayName("카테고리 생성 유저 ROLE ADMIN 아님 예외처리")
    void test4() {
        // given
        when(userRepository.findById(authenticatedUser.getUserId())).thenReturn(Optional.of(user2));
        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                categoryService.createCategory(authenticatedUser, categoryReq)
        );

        // then
        assertEquals(ExceptionEnum.NOT_ADMIN_ROLE, exception.getExceptionEnum());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 수정 시 해당 카테고리 아이디가 존재하지않음 예외처리")
    void test5() {
        // given
        when(userRepository.findById(authenticatedUser.getUserId())).thenReturn(Optional.of(user1));
        // when
        BaseException exception = assertThrows(BaseException.class, () ->
                categoryService.updateCategory(authenticatedUser,2L ,categoryReq)
        );
        // then
        assertEquals(ExceptionEnum.NOT_FOUNT_CATEGORY, exception.getExceptionEnum());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void test6() {
        // given
        when(userRepository.findById(authenticatedUser.getUserId())).thenReturn(Optional.of(user1));
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        // when
        categoryService.deleteCategory(authenticatedUser,category.getId());
        // then
        assertNotNull(category.getDeletedAt());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 조회 성공")
    void test7() {
        // given
        when(categoryRepository.findByDeletedAtIsNull()).thenReturn(List.of(category));

        List<CategoryRes> result = categoryService.getCategoryList();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(categoryRepository, never()).save(any(Category.class));
    }




}
