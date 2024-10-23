package com.sparta.gathering.domain.category.service;


import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.category.dto.request.CategoryReq;
import com.sparta.gathering.domain.category.dto.response.CategoryRes;
import com.sparta.gathering.domain.category.entity.Category;
import com.sparta.gathering.domain.category.repository.CategoryRepository;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 카테고리 생성
    @Transactional
    public CategoryRes createCategory(User user, CategoryReq categoryReq) {
        // 유저 권한 확인
        isValidUser(user);
        if (categoryRepository.findByCategoryName(categoryReq.getCategoryName()).isPresent()) {
            throw new BaseException(ExceptionEnum.ALREADY_HAVE_TITLE);
        }

        Category newCategory = Category.from(categoryReq, user);

        Category savedCategory = categoryRepository.save(newCategory);

        return CategoryRes.from(savedCategory);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(User token, UUID categoryId) {
        // 유저 권한 확인
        isValidUser(token);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.NOT_FOUNT_CATEGORY));
        category.updateDeleteAt();
    }


    // userRole ADMIN 확인
    public User isValidUser(User user) throws BaseException {
        User newuser = userRepository.findById(user.getId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        // 반대로 추가해야함 테스트용
        if (newuser.getUserRole().equals(UserRole.ROLE_ADMIN)) {
            throw new BaseException(ExceptionEnum.NOT_ADMIN_ROLE);
        }
        return newuser;
    }


}
