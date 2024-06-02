package com.example.multi_vendor.student

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multi_vendor.adapters.CategoryAdapter
import com.example.multi_vendor.adapters.ProductsAdapter
import com.example.multi_vendor.databinding.FragmentHomeBinding
import com.example.multi_vendor.models.Category
import com.example.multi_vendor.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val _binding get() = binding!!

    private lateinit var products: ArrayList<Product>
    private lateinit var filteredProducts: ArrayList<Product>
    private lateinit var onProductClick: ProductsAdapter.OnProductClick
    private lateinit var onCategoryClick: CategoryAdapter.OnCategoryClick

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        products = ArrayList()
        filteredProducts = ArrayList()

        val categories = listOf(
            Category(name = "All"),
            Category(name = "Electronics"),
            Category(name = "Shoes"),
            Category(name = "Fashion"),
            Category(name = "Edibles"),
            Category(name = "Furniture"),
            Category(name = "Beauty"),
        )

        onCategoryClick = object : CategoryAdapter.OnCategoryClick {
            override fun onCategoryClicked(position: Int) {
                val currentCategory = categories[position]
                val selectedCategory = currentCategory.name
                filterByCategory(selectedCategory)
            }

        }

        binding!!.categoriesRecycler.setHasFixedSize(true)
        val manger = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding!!.categoriesRecycler.layoutManager = manger
        binding!!.categoriesRecycler.adapter = CategoryAdapter(categories, onCategoryClick)


        binding!!.search.setOnClickListener {
            binding!!.search.visibility = View.GONE
            binding!!.text.visibility = View.GONE
            binding!!.searchView.visibility = View.VISIBLE
        }
        binding!!.productsView.setHasFixedSize(true)
//        binding!!.productsView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
//        binding!!.productsView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL))
        val layoutManager = GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
        binding!!.productsView.layoutManager = layoutManager

        binding!!.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchProducts(newText)
                return true
            }

        })

        onProductClick = object : ProductsAdapter.OnProductClick {
            override fun onProductClickListener(position: Int) {
                val currentProduct = filteredProducts[position]
                requireContext().startActivity(
                    Intent(requireContext(), ProductDetails::class.java)
                        .putExtra("productTitle", currentProduct.name)
                        .putExtra("description", currentProduct.description)
                        .putExtra("vendor", currentProduct.vendor)
                        .putExtra("price", currentProduct.price)
                        .putExtra("image", currentProduct.imageUrl)
                        .putExtra("phone", currentProduct.phone)
                        .putExtra("uid", currentProduct.uid)
                        .putExtra("productId", currentProduct.productId)
                        .putExtra("availability", currentProduct.availability)
                )
            }

        }

        FirebaseDatabase.getInstance().getReference("Products")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    products.clear()
                    snapshot.children.forEach { dataSnapshot ->
                        products.add(dataSnapshot.getValue(Product::class.java)!!)
                    }
                    filteredProducts.addAll(products)

                    if (filteredProducts.isNotEmpty()) {
                        binding!!.loading.visibility = View.GONE
                        binding!!.productsView.visibility = View.VISIBLE
                    } else {
                        binding!!.loading.visibility = View.GONE
                        binding!!.productsView.visibility = View.GONE
                        binding!!.empty.visibility = View.VISIBLE
                    }
                    binding!!.productsView.adapter = ProductsAdapter(
                        context = requireContext(),
                        onProductClick = onProductClick,
                        products = filteredProducts
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun filterByCategory(selectedCategory: String) {
        filteredProducts.clear()
        if (selectedCategory == "All") {
            filteredProducts.addAll(products)
            binding!!.productsView.adapter = ProductsAdapter(
                context = requireContext(),
                onProductClick = onProductClick,
                products = filteredProducts
            )
        } else {
            val selectedProducts = products.filter { it.category == selectedCategory }
            filteredProducts.addAll(selectedProducts)
            if (filteredProducts.isNotEmpty()) {
                binding!!.loading.visibility = View.GONE
                binding!!.productsView.visibility = View.VISIBLE
                binding!!.empty.visibility = View.GONE
            } else {
                binding!!.loading.visibility = View.GONE
                binding!!.productsView.visibility = View.GONE
                binding!!.empty.visibility = View.VISIBLE
            }
            binding!!.productsView.adapter = ProductsAdapter(
                context = requireContext(),
                onProductClick = onProductClick,
                products = filteredProducts
            )
        }

    }

    private fun searchProducts(newText: String) {
        filteredProducts.clear()
        if (newText.isEmpty()) {
            filteredProducts.addAll(products)
            binding!!.productsView.adapter = ProductsAdapter(requireContext(), onProductClick, filteredProducts)
        } else {
            val filteredList = products.filter { product ->
                product.name.contains(newText, ignoreCase = true) ||
                        product.vendor.contains(newText, ignoreCase = true)
            }
            filteredProducts.addAll(filteredList)
            binding!!.productsView.adapter = ProductsAdapter(
                context = requireContext(),
                onProductClick = onProductClick,
                products = filteredProducts
            )

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}