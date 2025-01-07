package com.example.service;

import com.example.entity.Product;
import com.example.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private static int totalTests = 0;
    private static int passedTests = 0;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Test Product", 100.0, 10);
        testProduct.setId(1L);
        System.out.println("\n------------------------------------");
    }

    @AfterEach
    void tearDown() {
        totalTests++;
        System.out.println("------------------------------------");
    }

    @AfterAll
    static void printTestSummary() {
        System.out.println("\n=== Test Summary ===");
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed Tests: " + passedTests);
        System.out.println("Failed Tests: " + (totalTests - passedTests));
        System.out.println("Success Rate: " + (passedTests * 100.0 / totalTests) + "%");
        System.out.println("===================\n");
    }

    private void testPassed(String testName) {
        passedTests++;
        System.out.println("✓ PASS: " + testName);
    }

    private void testFailed(String testName, Throwable e) {
        System.out.println("✗ FAIL: " + testName);
        System.out.println("Error: " + e.getMessage());
    }

    @Test
    @Order(1)
    void testSaveProduct_ValidProduct() {
        String testName = "Save Valid Product";
        System.out.println("Running Test: " + testName);
        
        try {
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);
            Product savedProduct = productService.saveProduct(testProduct);
            
            assertNotNull(savedProduct);
            assertEquals(testProduct.getName(), savedProduct.getName());
            verify(productRepository, times(1)).save(any(Product.class));
            
            testPassed(testName);
        } catch (AssertionError | Exception e) {
            testFailed(testName, e);
            throw e;
        }
    }

    @Test
    @Order(2)
    void testSaveProduct_NegativePrice() {
        String testName = "Save Product with Negative Price";
        System.out.println("Running Test: " + testName);
        
        try {
            Product invalidProduct = new Product("Invalid", -100.0, 10);
            
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                productService.saveProduct(invalidProduct);
            });
            
            assertEquals("Price cannot be negative", exception.getMessage());
            verify(productRepository, never()).save(any());
            
            testPassed(testName);
        } catch (AssertionError | Exception e) {
            testFailed(testName, e);
            throw e;
        }
    }

    @Test
    @Order(3)
    void testGetAllProducts() {
        String testName = "Get All Products";
        System.out.println("Running Test: " + testName);
        
        try {
            List<Product> productList = Arrays.asList(
                testProduct,
                new Product("Test Product 2", 200.0, 20)
            );
            when(productRepository.findAll()).thenReturn(productList);

            List<Product> result = productService.getAllProducts();
            
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(productRepository, times(1)).findAll();
            
            testPassed(testName);
        } catch (AssertionError | Exception e) {
            testFailed(testName, e);
            throw e;
        }
    }

    @Test
    @Order(4)
    void testGetProductById_Existing() {
        String testName = "Get Existing Product by ID";
        System.out.println("Running Test: " + testName);
        
        try {
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

            Product found = productService.getProductById(1L);
            
            assertNotNull(found);
            assertEquals(testProduct.getId(), found.getId());
            verify(productRepository, times(1)).findById(1L);
            
            testPassed(testName);
        } catch (AssertionError | Exception e) {
            testFailed(testName, e);
            throw e;
        }
    }

    @Test
    @Order(5)
    void testGetProductById_NonExisting() {
        String testName = "Get Non-existing Product by ID";
        System.out.println("Running Test: " + testName);
        
        try {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                productService.getProductById(99L);
            });
            
            assertEquals("Product not found", exception.getMessage());
            verify(productRepository, times(1)).findById(99L);
            
            testPassed(testName);
        } catch (AssertionError | Exception e) {
            testFailed(testName, e);
            throw e;
        }
    }

    @Test
    @Order(6)
    void testUpdateProduct_Success() {
        String testName = "Update Existing Product";
        System.out.println("Running Test: " + testName);
        
        try {
            when(productRepository.existsById(1L)).thenReturn(true);
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            Product updated = productService.updateProduct(testProduct);
            
            assertNotNull(updated);
            assertEquals(testProduct.getName(), updated.getName());
            verify(productRepository).existsById(1L);
            verify(productRepository).save(any(Product.class));
            
            testPassed(testName);
        } catch (AssertionError | Exception e) {
            testFailed(testName, e);
            throw e;
        }
    }

    @Test
    @Order(7)
    void testDeleteProduct() {
        String testName = "Delete Product";
        System.out.println("Running Test: " + testName);
        
        try {
            doNothing().when(productRepository).deleteById(1L);
            
            productService.deleteProduct(1L);
            
            verify(productRepository, times(1)).deleteById(1L);
            
            testPassed(testName);
        } catch (AssertionError | Exception e) {
            testFailed(testName, e);
            throw e;
        }
    }
}