package com.example.cloudgazer;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * This class tests the functionality of the ResultsPagerAdapter.
 * It verifies that the adapter correctly reports the number of items it manages,
 * based on the data it is initialized with.
 */
public class ResultsPagerAdapterTest {

    private ResultsPagerAdapter adapter;

    /**
     * Sets up the adapter with initial test data before each test.
     * This method initializes the adapter with two results and their corresponding descriptions.
     */
    @Before
    public void setUp() {
        String[] results = {"Result 1", "Result 2"};
        String[] descriptions = {"Description 1", "Description 2"};
        adapter = new ResultsPagerAdapter(results, descriptions);
    }

    /**
     * Tests whether getItemCount() correctly returns the number of items.
     * The expected outcome is that the number of items matches the length of the results array provided at setup.
     */
    @Test
    public void itemCount_matchesArrayLength() {
        // Check if getItemCount() returns the correct number of items
        assertEquals(2, adapter.getItemCount());
    }

    /**
     * Tests getItemCount() when the adapter is initialized with empty arrays.
     * The expected outcome is that the number of items reported is zero.
     */
    @Test
    public void itemCount_returnsZeroWhenArraysAreEmpty() {
        adapter = new ResultsPagerAdapter(new String[]{}, new String[]{});
        assertEquals(0, adapter.getItemCount());
    }

    /**
     * Tests getItemCount() when the adapter is initialized with a single item.
     * The expected outcome is that the number of items reported is one.
     */
    @Test
    public void itemCount_returnsCorrectCountWhenSingleItem() {
        adapter = new ResultsPagerAdapter(new String[]{"Result"}, new String[]{"Description"});
        assertEquals(1, adapter.getItemCount());
    }
}
