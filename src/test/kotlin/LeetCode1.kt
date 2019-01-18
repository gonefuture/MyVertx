import org.junit.Test

/**
 * @author gonefuture  gonefuture@qq.com
 * @time 2018/7/24 19:15
 * @version 1.00
 * Description: MyVertx
 */
class LeetCode1 {


    @Test fun test() {
        println(Solution().twoSum( intArrayOf(2, 7, 11, 15),9).forEach { println(it) })
    }
}

class Solution {
    fun twoSum(nums: IntArray, target: Int) : IntArray {
        for( i in nums.indices) {
            for( j in nums.indices) {
                if (i != j && (nums[i] + nums[j]) == target) return intArrayOf(i, j)
            }
        }

        return intArrayOf()
    }
}
