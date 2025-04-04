package com.example.cookeasy

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecipeCard(
    recipe: Recipe,
    isSaved: Boolean,
    onSaveClick: (Recipe, Boolean) -> Unit,
    onRecipeClick: (Int) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isSavedState = remember { mutableStateOf(isSaved) }
    LaunchedEffect(isSaved) {
        isSavedState.value = isSaved
    }

    val heartScale = remember { Animatable(1f) }
    LaunchedEffect(isSavedState.value) {
        if (isSavedState.value) {
            heartScale.animateTo(
                targetValue = 1.3f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = 10000f // Custom very high stiffness for fast animation
                )
            )
            heartScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = 10000f // Custom very high stiffness for fast animation
                )
            )
        }
    }

    var isPressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(50), // Very fast scaling: 50ms
        label = "cardScale"
    )
    val cardColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFFF5722) else Color.White,
        animationSpec = tween(50), // Very fast color change: 50ms
        label = "cardColor"
    )

    val coroutineScope = rememberCoroutineScope()
    val accentColor = Color(0xFFFF5722)
    val textPrimaryColor = Color(0xFF212121)
    val textSecondaryColor = Color(0xFF757575)

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(260.dp)
            .padding(4.dp)
            .graphicsLayer {
                scaleX = cardScale
                scaleY = cardScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                isPressed = true
                coroutineScope.launch {
                    onRecipeClick(recipe.id) // Immediate click response
                    delay(50) // Minimal delay for visual feedback
                    isPressed = false
                }
            },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.2f))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Recipe Image: ${recipe.title}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recipe.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = textPrimaryColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            recipe.category?.let { category ->
                Text(
                    text = category,
                    fontSize = 12.sp,
                    color = accentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            } ?: Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer {
                            scaleX = heartScale.value
                            scaleY = heartScale.value
                        }
                        .clip(CircleShape)
                        .background(
                            if (isSavedState.value) accentColor.copy(alpha = 0.1f)
                            else Color.Transparent
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current // Use default ripple effect
                        ) {
                            onSaveClick(recipe, isSavedState.value)
                            isSavedState.value = !isSavedState.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSavedState.value) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isSavedState.value) "Unsave" else "Save",
                        tint = if (isSavedState.value) accentColor else textSecondaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreenUI(
    isLoading: Boolean,
    isLoadingMore: Boolean,
    filteredRecipes: List<Recipe>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    savedRecipeIds: Set<Int>,
    onSaveClick: (Recipe, Boolean) -> Unit,
    onRecipeClick: (Int) -> Unit,
    errorMessage: String?,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val scrollUpState = remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val topBarElevation by animateDpAsState(
        targetValue = if (scrollUpState.value) 8.dp else 2.dp,
        label = "topBarElevation"
    )

    var expandedCategoryDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val totalItems = listState.layoutInfo.totalItemsCount
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                if (totalItems > 0 && lastVisibleItem >= totalItems - 5) {
                    onLoadMore()
                }
            }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)) + expandVertically(animationSpec = tween(500))
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 0.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.wrapContentWidth()
                            ) {
                                Text(
                                    "CookEasy",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                val pulseAnimation = rememberInfiniteTransition()
                                val scale by pulseAnimation.animateFloat(
                                    initialValue = 0.8f,
                                    targetValue = 1.2f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(800),
                                        repeatMode = RepeatMode.Reverse
                                    )
                                )

                                Box(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .size(6.dp)
                                        .scale(scale)
                                        .background(Color(0xFF4CAF50), CircleShape)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFF5722),
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier
                        .shadow(topBarElevation)
                        .height(48.dp)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    shape = CircleShape,
                    containerColor = Color(0xFFFF5722),
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Scroll to top"
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        expandedCategoryDropdown = false
                        focusManager.clearFocus()
                    }
            ) {
                // Replace the existing search bar Row with MemeSearchBarTile
                MemeSearchBarTile(
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    selectedCategory = selectedCategory,
                    onCategorySelected = onCategorySelected,
                    expandedCategoryDropdown = expandedCategoryDropdown,
                    onExpandedCategoryDropdownChange = { expandedCategoryDropdown = it }
                )

                AnimatedVisibility(
                    visible = selectedCategory != "All",
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = {
                                expandedCategoryDropdown = true
                            },
                            label = { Text("Category: $selectedCategory") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Category,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5722),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear category",
                                    tint = Color(0xFFFF5722),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            onCategorySelected("All")
                                            expandedCategoryDropdown = true
                                        }
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFFF5722).copy(alpha = 0.1f),
                                labelColor = Color(0xFFFF5722)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = Color(0xFFFF5722).copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                AnimatedContent(
                    targetState = Triple(isLoading, errorMessage, filteredRecipes.isEmpty()),
                    transitionSpec = { fadeIn() with fadeOut() }
                ) { (loading, error, isEmpty) ->
                    when {
                        loading -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                CircularProgressIndicator(color = Color(0xFFFF5722))
                            }
                        }
                        error != null -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.Error,
                                        contentDescription = "Error",
                                        tint = Color.Red,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(text = error, color = Color.Red)
                                }
                            }
                        }
                        isEmpty -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = "No Results",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No recipes found. Try a different search or category.",
                                        color = Color.Gray,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                        }
                        else -> {
                            LazyColumn(
                                state = listState,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White)
                            ) {
                                items(filteredRecipes.chunked(2)) { recipePair ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        recipePair.forEach { recipe ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                RecipeCard(
                                                    recipe = recipe,
                                                    isSaved = savedRecipeIds.contains(recipe.id),
                                                    onSaveClick = onSaveClick,
                                                    onRecipeClick = onRecipeClick
                                                )
                                            }
                                        }
                                        if (recipePair.size < 2) {
                                            Box(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                                if (isLoadingMore) {
                                    item {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                color = Color(0xFFFF5722),
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeSearchBarTile(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    expandedCategoryDropdown: Boolean,
    onExpandedCategoryDropdownChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val searchBarFocused = remember { mutableStateOf(false) }

    // Animation for a subtle scale effect on click
    var isClicked by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isClicked) 0.90f else 1f, // More pronounced shrink for larger size
        animationSpec = tween(durationMillis = 200),
        label = "memeTileScale"
    )

    // Animation for focus/hover effect
    val searchFieldScale by animateFloatAsState(
        targetValue = if (searchBarFocused.value) 1.04f else 1f, // Larger scale on focus
        animationSpec = tween(durationMillis = 200),
        label = "searchFieldScale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp) // Increased padding
            .background(Color.Transparent, shape = RoundedCornerShape(20.dp)) // Transparent background
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isClicked = !isClicked
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing
    ) {
        // Search input field
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search 🥐",
                    style = MaterialTheme.typography.titleMedium, // Even larger text
                    color = Color.Gray.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search Icon",
                    tint = Color(0xFFFF7043),
                    modifier = Modifier.size(28.dp) // Larger icon
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear search",
                            tint = Color(0xFFFF7043),
                            modifier = Modifier.size(28.dp) // Larger icon
                        )
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(70.dp) // Increased height
                .scale(searchFieldScale)
                .padding(start = 16.dp, top = 10.dp, bottom = 10.dp) // Increased padding
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)) // Larger corner radius
                .onFocusChanged {
                    searchBarFocused.value = it.isFocused
                    if (it.isFocused) {
                        onExpandedCategoryDropdownChange(false)
                    }
                },
            textStyle = MaterialTheme.typography.titleMedium.copy(color = Color.Black), // Larger text
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFFF7043),
                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.3f),
                focusedContainerColor = Color.Transparent, // Updated parameter
                unfocusedContainerColor = Color.Transparent, // Updated parameter
                focusedLabelColor = Color(0xFFFF7043),
                cursorColor = Color(0xFFFF7043)
            ),
            singleLine = true
        )

        // Category dropdown
        Box(
            modifier = Modifier
                .height(70.dp) // Increased height
                .width(140.dp) // Increased width
                .padding(end = 16.dp) // Increased padding
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onExpandedCategoryDropdownChange(!expandedCategoryDropdown)
                        focusManager.clearFocus()
                    },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp), // Increased padding
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedCategory == "All") "All" else selectedCategory,
                        style = MaterialTheme.typography.titleMedium, // Larger text
                        color = Color.Black
                    )
                    Icon(
                        imageVector = if (expandedCategoryDropdown)
                            Icons.Filled.KeyboardArrowUp
                        else
                            Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Toggle category dropdown",
                        tint = Color(0xFFFF7043),
                        modifier = Modifier.size(28.dp) // Larger icon
                    )
                }
            }

            DropdownMenu(
                expanded = expandedCategoryDropdown,
                onDismissRequest = { onExpandedCategoryDropdownChange(false) },
                modifier = Modifier
                    .width(160.dp) // Increased width to match larger design
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .shadow(4.dp),
                properties = PopupProperties(focusable = true)
            ) {
                val categories = listOf(
                    "All", "Chicken", "Beef", "Dessert", "Seafood", "Vegetarian",
                    "Pasta", "Pork", "Breakfast", "Side", "Soup", "Salad", "Lamb",
                    "Miscellaneous", "Starter"
                )

                categories.forEach { category ->
                    val isSelected = category == selectedCategory
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category,
                                color = if (isSelected) Color(0xFFFF7043) else Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.titleMedium // Larger text
                            )
                        },
                        onClick = {
                            onCategorySelected(category)
                            if (category == "All") {
                                onExpandedCategoryDropdownChange(true)
                            } else {
                                onExpandedCategoryDropdownChange(false)
                            }
                        },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color(0xFFFF7043),
                                    modifier = Modifier.size(28.dp) // Larger icon
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                    )
                }
            }
        }
    }
}