package org.javers.test.builder;

import org.javers.model.mapping.Category;

/**
 * @author Pawel Cierpiatka
 */
public class CategoryTestBuilder {

    private Category category;

    public static CategoryTestBuilder category() {
        return new CategoryTestBuilder();
    }


    public CategoryTestBuilder deepWithChildNumber(int level, int numberOfChild) {
        category = new Category();
        category.setId(0L);
        category.setName("root");
        create(category, level, numberOfChild);

        return this;

    }

    private void create(Category cat, int level, int numberOfChild) {

        if(level <= 0){
            return;
        }
        createCategoryChild(cat, numberOfChild, level);
        for(Category c : cat.getCategorys()) {
            create(c, level - 1, numberOfChild);
        }
    }

    private void createCategoryChild(Category root, int numberOfChild, int level) {
        for (int i = 0; i<numberOfChild; i++) {
            Category child = new Category();
            child.setId(Long.valueOf(root.getId() + i + 1) * level);
            child.setName("Name " + child.getId());
            root.addChild(child);
        }
    }

    public Category build() {
        return category;
    }

}
