package com.ecomarket.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Embedded
import kotlinx.coroutines.flow.Flow

data class CartLine(
    @Embedded val product: ProductEntity,
    val qty: Int
)

@Dao
interface CartDao {

    @Query("""
        SELECT p.*, c.qty AS qty
        FROM cart_items c
        INNER JOIN products p ON p.id = c.productId
        ORDER BY p.name
    """)
    fun observeCart(): Flow<List<CartLine>>

    @Query("""
        SELECT SUM(
            (CASE WHEN p.discountPercent IS NULL
                  THEN p.price
                  ELSE p.price * (1 - p.discountPercent/100.0)
             END) * c.qty
        )
        FROM cart_items c
        JOIN products p ON p.id = c.productId
    """)
    fun observeSubtotal(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    // Devuelve filas afectadas (0 = el item no existía)
    @Query("UPDATE cart_items SET qty = qty + :delta WHERE productId = :productId")
    suspend fun changeQty(productId: String, delta: Int): Int

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun remove(productId: String)

    @Query("DELETE FROM cart_items WHERE qty <= 0")
    suspend fun pruneZeros()

    @Query("DELETE FROM cart_items")
    suspend fun clear()
}
