package org.javers.test.builder;

import org.javers.model.mapping.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pawel Cierpiatka
 */
public class CategoryTestBuilder {
    private static final Logger logger = LoggerFactory.getLogger(CategoryTestBuilder.class);

    private Category root;
    private long nodes;
    private int idMultiplier;

    private CategoryTestBuilder(int idMultiplier) {
        this.idMultiplier = idMultiplier;
    }

    public static CategoryTestBuilder category() {
        return new CategoryTestBuilder(1);
    }

    public static CategoryTestBuilder category(int idMultiplier) {
        return new CategoryTestBuilder(idMultiplier);
    }

    public CategoryTestBuilder deepWithChildNumber(int level, int numberOfChild) {
        root = crateCategory("root");
        create(root, level, numberOfChild);
        return this;
    }

    private Category crateCategory(String namePrefix) {
        nodes++;
        return new Category(nodes*idMultiplier, namePrefix+" "+nodes*idMultiplier);
    }

    private void create(Category cat, int level, int numberOfChild) {
        if(level <= 0){
            return;
        }
        createCategoryChildren(cat, numberOfChild, level);
        for(Category c : cat.getCategories()) {
            create(c, level - 1, numberOfChild);
        }
    }

    private void createCategoryChildren(Category parent, int numberOfChild, int level) {
        for (int i = 0; i<numberOfChild; i++) {
            Category child =  crateCategory("Name");
            parent.addChild(child);
        }
    }

    public Category build() {
        logger.info("created Category tree with "+nodes+" nodes");
        return root;
    }

}
