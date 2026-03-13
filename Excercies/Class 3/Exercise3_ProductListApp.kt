// SE 3242: Android Application Development
// Week 3: Kotlin Essentials - Exercise 3
// Exercise 3: Product List with Details (Jetpack Compose App)

/*
 * NOTE: This is a complete Jetpack Compose application.
 * To run this code, you need an Android project with Compose dependencies.
 * 
 * Required dependencies in build.gradle:
 * implementation "androidx.compose.ui:ui:1.5.0"
 * implementation "androidx.compose.material3:material3:1.1.0"
 * implementation "androidx.navigation:navigation-compose:2.7.0"
 * implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0"
 */

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// ========================================
// Data Model
// ========================================

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String
)

// ========================================
// ViewModel
// ========================================

class ProductViewModel : ViewModel() {
    // Hardcoded list of products
    private val _products = listOf(
        Product(1, "Laptop", 999.99, "High-performance laptop with 16GB RAM"),
        Product(2, "Smartphone", 699.99, "Latest smartphone with 5G support"),
        Product(3, "Headphones", 199.99, "Wireless noise-cancelling headphones"),
        Product(4, "Tablet", 499.99, "10-inch tablet with stylus support"),
        Product(5, "Smartwatch", 299.99, "Fitness tracking smartwatch"),
        Product(6, "Camera", 1299.99, "Professional mirrorless camera"),
        Product(7, "Keyboard", 149.99, "Mechanical gaming keyboard"),
        Product(8, "Mouse", 79.99, "Wireless ergonomic mouse"),
        Product(9, "Monitor", 449.99, "27-inch 4K monitor"),
        Product(10, "Speaker", 249.99, "Bluetooth portable speaker")
    )
    
    val products: List<Product> = _products
    
    // Cart state
    private val _cart = mutableStateListOf<Product>()
    val cart: List<Product> = _cart
    
    fun addToCart(product: Product) {
        _cart.add(product)
    }
    
    fun getProductById(id: Int): Product? {
        return _products.find { it.id == id }
    }
    
    fun getCartTotal(): Double {
        return _cart.sumOf { it.price }
    }
}

// ========================================
// Main App Composable
// ========================================

@Composable
fun ProductApp(viewModel: ProductViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val navController = rememberNavController()
    
    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = "product_list"
        ) {
            composable("product_list") {
                ProductListScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
            
            composable(
                route = "product_detail/{productId}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                ProductDetailScreen(
                    productId = productId,
                    navController = navController,
                    viewModel = viewModel
                )
            }
            
            composable("cart") {
                CartScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}

// ========================================
// Product List Screen
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product List") },
                actions = {
                    // Cart icon with badge
                    IconButton(onClick = { navController.navigate("cart") }) {
                        BadgedBox(
                            badge = {
                                if (viewModel.cart.isNotEmpty()) {
                                    Badge { Text("${viewModel.cart.size}") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            itemsIndexed(viewModel.products) { index, product ->
                ProductListItem(
                    product = product,
                    onClick = {
                        navController.navigate("product_detail/${product.id}")
                    }
                )
                
                if (index < viewModel.products.size - 1) {
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${product.price}",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details"
            )
        }
    }
}

// ========================================
// Product Detail Screen
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    navController: NavController,
    viewModel: ProductViewModel
) {
    val product = viewModel.getProductById(productId)
    var showSnackbar by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text("${product?.name} added to cart!")
                }
            }
        }
    ) { paddingValues ->
        if (product != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Product name
                Text(
                    text = product.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Price
                Text(
                    text = "Price: $${product.price}",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Description
                Text(
                    text = "Description",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = product.description,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Add to Cart button
                Button(
                    onClick = {
                        viewModel.addToCart(product)
                        showSnackbar = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Add to Cart",
                        fontSize = 18.sp
                    )
                }
            }
        } else {
            // Product not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Product not found")
            }
        }
    }
}

// ========================================
// Cart Screen
// ========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: ProductViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping Cart") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.cart.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your cart is empty",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(viewModel.cart) { product ->
                        CartItem(product = product)
                        Divider()
                    }
                }
                
                // Total
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format("%.2f", viewModel.getCartTotal())}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Button(
                    onClick = { /* Handle checkout */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(56.dp)
                ) {
                    Text("Checkout", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun CartItem(product: Product) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = product.description,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
        Text(
            text = "$${product.price}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// ========================================
// Missing Icons (Add these imports in real project)
// ========================================

// These would normally come from androidx.compose.material.icons.filled
object Icons {
    object Default {
        val ShoppingCart = androidx.compose.material.icons.Icons.Default.ShoppingCart
        val ChevronRight = androidx.compose.material.icons.Icons.Default.ChevronRight
        val ArrowBack = androidx.compose.material.icons.Icons.Default.ArrowBack
        val AddShoppingCart = androidx.compose.material.icons.Icons.Default.AddShoppingCart
    }
}
