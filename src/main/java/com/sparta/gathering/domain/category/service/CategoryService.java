package com.sparta.gathering.domain.category.service;


import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.category.dto.request.CategoryReq;
import com.sparta.gathering.domain.category.dto.response.CategoryRes;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.category.repository.CategoryRepository;
import com.sparta.gathering.domain.user.dto.response.UserDTO;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 카테고리 생성
    @Transactional
    public CategoryRes createCategory(UserDTO userDto, CategoryReq categoryReq) {
        // 유저 권한 확인
        User user = isValidUser(userDto);
        if (categoryRepository.findByCategoryName(categoryReq.getCategoryName()).isPresent()) {
            throw new BaseException(ExceptionEnum.ALREADY_HAVE_CATEGORY);
        }

        Category newCategory = Category.from(categoryReq, user);
        Category savedCategory = categoryRepository.save(newCategory);
        return CategoryRes.from(savedCategory);
    }

    // 카테고리 조회
    public List<CategoryRes> getCategoryList() {
        return categoryRepository.findByDeletedAtIsNull()
                .stream()
                .map(CategoryRes::from)
                .toList();
    }

    // 카테고리 수정
    @Transactional
    public CategoryRes updateCategory(UserDTO userDto, Long categoryId, CategoryReq categoryReq) {
        User newuser = isValidUser(userDto);
        Category category = isValidCategory(categoryId);

        category.updateCategory(categoryReq.getCategoryName(), newuser);
        Category savedCategory = categoryRepository.save(category);
        return CategoryRes.from(savedCategory);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(UserDTO user, Long categoryId) {
        // 유저 권한 확인
        isValidUser(user);
        Category category = isValidCategory(categoryId);
        category.updateDeleteAt();
    }


    // userRole ADMIN 확인
    public User isValidUser(UserDTO userDto) throws BaseException {
        User newuser = userRepository.findById(userDto.getUserId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (!newuser.getUserRole().equals(UserRole.ROLE_ADMIN)) {
            throw new BaseException(ExceptionEnum.NOT_ADMIN_ROLE);
        }
        return newuser;
    }

    // 카테고리 아이디 확인
    public Category isValidCategory(Long categoryId) throws BaseException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_CATEGORY));
    }

}
