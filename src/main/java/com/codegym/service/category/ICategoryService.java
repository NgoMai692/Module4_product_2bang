package com.codegym.service.category;

import com.codegym.model.Category;
import com.codegym.service.IGeneralService;

public interface ICategoryService extends IGeneralService<Category> {
    Iterable<Category> findAll();

    Iterable<Category> findByNameContaining(String name);
}
