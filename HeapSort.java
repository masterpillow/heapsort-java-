import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * HeapSort Implementation for String Arrays (and List<String>)
 *
 * ─────────────────────────────────────────────────────────────────
 * HOW HEAPSORT WORKS:
 *   HeapSort operates in two phases:
 *
 *   Phase 1 — Build a Max-Heap (or Min-Heap):
 *     Starting from the last non-leaf node down to the root, call
 *     heapify() on each node to establish the heap property.
 *     A Max-Heap guarantees that every parent node is >= its children.
 *
 *   Phase 2 — Extract elements from the heap:
 *     Repeatedly swap the root (the largest/smallest element) with the
 *     last unsorted element, reduce the heap size by one, and call
 *     heapify() on the new root to restore the heap property.
 *     After n-1 extractions, the array is fully sorted.
 *
 * WHY O(n log n)?
 *   - buildHeap()  : O(n)       — amortized linear-time heap construction
 *   - heapify()    : O(log n)   — traverses at most one root-to-leaf path
 *   - Extraction loop runs n-1 times, each calling heapify → O(n log n)
 *   - Total: O(n) + O(n log n) = O(n log n) in all cases (best/avg/worst)
 *   - Space: O(1) auxiliary — fully in-place, only stack frames for recursion
 * ─────────────────────────────────────────────────────────────────
 */
public class HeapSort {

    // ─────────────────────────────────────────────────────────────
    //  PUBLIC API — Array version
    // ─────────────────────────────────────────────────────────────

    /**
     * Sorts a String array using the HeapSort algorithm.
     *
     * @param arr   the array to sort (modified in-place)
     * @param order 0 → ascending order; any other value → descending order
     */
    public static void HeapSort(String[] arr, int order) {
        if (arr == null || arr.length < 2) return; // nothing to sort

        int n = arr.length;

        // Phase 1: Build the heap in the array.
        //   For ascending  sort we build a MAX-heap (largest root → sinks to end).
        //   For descending sort we build a MIN-heap (smallest root → sinks to end).
        buildHeap(arr, n, order);

        // Phase 2: Extract elements one by one from the heap.
        //   Each iteration shrinks the "heap region" by one position.
        for (int i = n - 1; i > 0; i--) {
            // Move the current root (max or min) to its final sorted position.
            swap(arr, 0, i);

            // Restore the heap property for the reduced heap [0 .. i-1].
            heapify(arr, i, 0, order);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  PUBLIC API — List<String> overload
    // ─────────────────────────────────────────────────────────────

    /**
     * Convenience overload: sorts a List<String> in-place using HeapSort.
     * Internally converts to an array, sorts, then writes values back.
     *
     * @param list  the list to sort (modified in-place)
     * @param order 0 → ascending; any other value → descending
     */
    public static void HeapSort(List<String> list, int order) {
        if (list == null || list.size() < 2) return;

        // Extract to array, sort, write back — still O(n log n), O(n) extra space
        // for the temporary array (unavoidable with List's index-based access).
        String[] arr = list.toArray(new String[0]);
        HeapSort(arr, order);
        for (int i = 0; i < arr.length; i++) {
            list.set(i, arr[i]);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────

    /**
     * Builds the initial heap structure over the entire array.
     *
     * We start from the last non-leaf node (index n/2 - 1) and call
     * heapify() upward to the root. This gives O(n) construction time
     * (as opposed to O(n log n) if we inserted elements one by one).
     *
     * @param arr   the array to heapify
     * @param n     the number of elements to consider
     * @param order sort direction flag (passed through to heapify)
     */
    private static void buildHeap(String[] arr, int n, int order) {
        // Last non-leaf node is at index (n / 2 - 1).
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i, order);
        }
    }

    /**
     * Restores the heap property for a subtree rooted at index {@code root}.
     *
     * For ascending  sort (MAX-heap): the largest child wins promotion.
     * For descending sort (MIN-heap): the smallest child wins promotion.
     *
     * The method follows the "sift-down" (push-down) strategy: compare
     * the root with its children; if the heap property is violated, swap
     * with the dominant child and recurse (iteratively) down the tree.
     *
     * Time per call: O(log n)  — at most one path from root to leaf.
     *
     * @param arr   the array containing the heap
     * @param n     the current heap size (elements beyond n are sorted)
     * @param root  index of the subtree root to sift down
     * @param order sort direction flag
     */
    private static void heapify(String[] arr, int n, int root, int order) {
        int dominant = root;       // Tracks the index of the "winner" node
        int left     = 2 * root + 1;
        int right    = 2 * root + 2;

        // Check whether the left child should dominate the current root.
        if (left < n && shouldSwap(arr[dominant], arr[left], order)) {
            dominant = left;
        }

        // Check whether the right child should dominate the current dominant.
        if (right < n && shouldSwap(arr[dominant], arr[right], order)) {
            dominant = right;
        }

        // If the dominant node is not the root, fix the violation and recurse.
        if (dominant != root) {
            swap(arr, root, dominant);
            heapify(arr, n, dominant, order); // Tail-recursive; JVM may optimize.
        }
    }

    /**
     * Determines whether {@code parent} should be swapped with {@code child}
     * based on the current sort order.
     *
     * This method centralizes all comparison logic, keeping it reusable and
     * independent of any particular caller.
     *
     * @param parent the element currently occupying the parent position
     * @param child  the element occupying a child position
     * @param order  0 → MAX-heap logic (ascending sort);
     *               non-zero → MIN-heap logic (descending sort)
     * @return {@code true} if a swap is needed to restore heap property
     */
    private static boolean shouldSwap(String parent, String child, int order) {
        int cmp = parent.compareTo(child); // lexicographic comparison

        if (order == 0) {
            // MAX-heap: parent must be >= child; swap if child is larger.
            return cmp < 0;
        } else {
            // MIN-heap: parent must be <= child; swap if child is smaller.
            return cmp > 0;
        }
    }

    /**
     * Swaps two elements in the array in O(1) time.
     *
     * @param arr the array
     * @param i   index of the first element
     * @param j   index of the second element
     */
    private static void swap(String[] arr, int i, int j) {
        String temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // ─────────────────────────────────────────────────────────────
    //  MAIN — demonstration & test cases
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {

        // ── Test 1: Ascending sort on a String array ──────────────
        System.out.println("=== Test 1: Ascending Sort (Array) ===");
        String[] arr1 = {"banana", "apple", "cherry", "date", "elderberry", "fig"};
        System.out.println("Before : " + Arrays.toString(arr1));
        HeapSort(arr1, 0);                        // order == 0 → ascending
        System.out.println("After  : " + Arrays.toString(arr1));

        System.out.println();

        // ── Test 2: Descending sort on a String array ─────────────
        System.out.println("=== Test 2: Descending Sort (Array) ===");
        String[] arr2 = {"banana", "apple", "cherry", "date", "elderberry", "fig"};
        System.out.println("Before : " + Arrays.toString(arr2));
        HeapSort(arr2, 1);                        // order != 0 → descending
        System.out.println("After  : " + Arrays.toString(arr2));

        System.out.println();

        // ── Test 3: Ascending sort on a List<String> ─────────────
        System.out.println("=== Test 3: Ascending Sort (List<String>) ===");
        List<String> list1 = new ArrayList<>(List.of("mango", "kiwi", "peach", "avocado", "lime"));
        System.out.println("Before : " + list1);
        HeapSort(list1, 0);
        System.out.println("After  : " + list1);

        System.out.println();

        // ── Test 4: Descending sort on a List<String> ────────────
        System.out.println("=== Test 4: Descending Sort (List<String>) ===");
        List<String> list2 = new ArrayList<>(List.of("mango", "kiwi", "peach", "avocado", "lime"));
        System.out.println("Before : " + list2);
        HeapSort(list2, -1);
        System.out.println("After  : " + list2);
    }
}
