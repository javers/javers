package org.javers.test.builder

import org.javers.core.model.Category

/**
 * @author bartosz walacik
 */
class CategoryTestBuilder {
    private Category root
    private long nodes
    private int idMultiplier

    private CategoryTestBuilder(int idMultiplier) {
        this.idMultiplier = idMultiplier
    }

    static CategoryTestBuilder category() {
        new CategoryTestBuilder(1)
    }

    static CategoryTestBuilder category(int idMultiplier) {
        new CategoryTestBuilder(idMultiplier)
    }

    CategoryTestBuilder deepWithChildNumber(int level, int numberOfChild) {
        deepWithChildNumber(level, numberOfChild, "node")
    }

    CategoryTestBuilder deepWithChildNumber(int level, int numberOfChild, String namePrefix) {
        root = crateCategory(namePrefix)
        create(root, level, numberOfChild, namePrefix)
        this
    }

    /**
     * recursive
     */
    private void create(Category cat, int level, int numberOfChild, String namePrefix) {
        if(level <= 0){
            return
        }
        createCategoryChildren(cat, numberOfChild, namePrefix)
        cat.categories.each {c ->  create(c, level - 1, numberOfChild, namePrefix)}
    }

    private void createCategoryChildren(Category parent, int numberOfChild, String namePrefix) {
        (0..numberOfChild-1).each {
            Category child =  crateCategory(namePrefix)
            parent.addChild(child)
        }
    }

    private Category crateCategory(String namePrefix) {
        nodes++
        new Category(nodes*idMultiplier, namePrefix+" "+nodes*idMultiplier)
    }

    Category build() {
        println("created Category tree with "+nodes+" nodes")
        root
    }
}
