package com.ecomarket.data

object Seed {
    fun products(): List<ProductEntity> = listOf(
        ProductEntity(
            id = "p1",
            name = "Jugo Natural de Naranja",
            price = 2490.0,
            imageUrl = "https://images.unsplash.com/photo-1524594224033-6c8fef0a1a8e",
            category = "Bebidas",
            description = "Jugo 100% natural, sin azúcar añadida.",
            discountPercent = 10
        ),
        ProductEntity(
            id = "p2",
            name = "Mix Frutas Frescas",
            price = 3990.0,
            imageUrl = "https://images.unsplash.com/photo-1576402187878-974f70c890a5",
            category = "Frutas",
            description = "Manzanas, plátanos y berries seleccionados.",
            discountPercent = null
        ),
        ProductEntity(
            id = "p3",
            name = "Pan Integral",
            price = 1890.0,
            imageUrl = "https://images.unsplash.com/photo-1608198093002-ad4e005484ec",
            category = "Panadería",
            description = "Pan artesanal integral de masa madre.",
            discountPercent = 5
        ),
        ProductEntity(
            id = "p4",
            name = "Leche Descremada 1L",
            price = 1390.0,
            imageUrl = "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b",
            category = "Lácteos",
            description = "Leche descremada sin lactosa.",
            discountPercent = null
        ),
        ProductEntity(
            id = "p5",
            name = "Zanahorias Orgánicas",
            price = 990.0,
            imageUrl = "https://images.unsplash.com/photo-1568605114967-8130f3a36994",
            category = "Verduras",
            description = "Cultivo local, frescas y crujientes.",
            discountPercent = null
        )
    )
}
