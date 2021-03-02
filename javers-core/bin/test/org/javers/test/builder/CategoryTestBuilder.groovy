package org.javers.test.builder

import org.javers.core.model.CategoryC

/**
 * @author bartosz walacik
 */
class CategoryTestBuilder {
    private CategoryC root
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
    private void create(CategoryC cat, int level, int numberOfChild, String namePrefix) {
        if(level <= 0){
            return
        }
        createCategoryChildren(cat, numberOfChild, namePrefix)
        cat.categories.each {c ->  create(c, level - 1, numberOfChild, namePrefix)}
    }

    private void createCategoryChildren(CategoryC parent, int numberOfChild, String namePrefix) {
        (0..numberOfChild-1).each {
            CategoryC child =  crateCategory(namePrefix)
            parent.addChild(child)
        }
    }

    private CategoryC crateCategory(String namePrefix) {
        nodes++
        new CategoryC(nodes*idMultiplier, namePrefix+" "+nodes*idMultiplier)
    }

    CategoryC build() {
        println("created CategoryC tree with "+nodes+" nodes")
        root
    }
}
