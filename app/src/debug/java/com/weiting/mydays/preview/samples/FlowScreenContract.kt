package com.weiting.mydays.preview.samples

/**
 * FlowScreen Contract
 *
 * 定義 FlowScreen 的 state、actions、events
 * 方便之後轉換到正式 app
 */

/**
 * Filter 類型
 */
enum class FilterType(val displayName: String) {
    ALL("全部"),
    DIARY("日記"),
    EXPENSE("記帳"),
    HABIT("習慣")
}

/**
 * 記錄類型
 */
sealed class RecordType {
    data class Habit(
        val id: String,
        val name: String,
        val completed: Boolean
    ) : RecordType()

    data class Expense(
        val id: String,
        val amount: Int,
        val category: String
    ) : RecordType()

    data class Diary(
        val id: String,
        val preview: String
    ) : RecordType()
}

/**
 * 時間軸記錄
 */
data class Record(
    val id: String,
    val type: RecordType,
    val time: String,
    val date: String
)

/**
 * FlowScreen State
 */
data class FlowScreenState(
    val selectedFilter: FilterType = FilterType.ALL,
    val records: List<Record> = emptyList(),
    val activityMap: Map<Int, Int> = emptyMap(),  // date -> activity count
    val currentMonth: String = "8月"
)

/**
 * FlowScreen Actions
 */
interface FlowScreenActions {
    fun onFilterSelected(filter: FilterType)
    fun onRecordClicked(recordId: String)
    fun onDateCellClicked(date: Int)
}

/**
 * Mock data 產生器
 */
object FlowScreenMockData {
    fun createMockState(): FlowScreenState {
        return FlowScreenState(
            selectedFilter = FilterType.ALL,
            records = createMockRecords(),
            activityMap = createMockActivityMap(),
            currentMonth = "8月"
        )
    }

    private fun createMockRecords(): List<Record> {
        return listOf(
            // 今天
            Record(
                id = "1",
                type = RecordType.Habit(
                    id = "h1",
                    name = "晨間運動",
                    completed = true
                ),
                time = "08:30",
                date = "今天"
            ),
            Record(
                id = "2",
                type = RecordType.Expense(
                    id = "e1",
                    amount = -120,
                    category = "飲食"
                ),
                time = "12:15",
                date = "今天"
            ),
            Record(
                id = "3",
                type = RecordType.Habit(
                    id = "h2",
                    name = "閱讀 30 分鐘",
                    completed = true
                ),
                time = "21:00",
                date = "今天"
            ),

            // 昨天
            Record(
                id = "4",
                type = RecordType.Diary(
                    id = "d1",
                    preview = "今天天氣很好，心情也不錯。下午去了公園散步，看到很多人在運動..."
                ),
                time = "22:30",
                date = "昨天"
            ),
            Record(
                id = "5",
                type = RecordType.Habit(
                    id = "h3",
                    name = "冥想 10 分鐘",
                    completed = true
                ),
                time = "20:00",
                date = "昨天"
            ),
            Record(
                id = "6",
                type = RecordType.Expense(
                    id = "e2",
                    amount = -250,
                    category = "飲食"
                ),
                time = "18:45",
                date = "昨天"
            ),
            Record(
                id = "7",
                type = RecordType.Expense(
                    id = "e3",
                    amount = -20,
                    category = "交通"
                ),
                time = "09:00",
                date = "昨天"
            ),

            // 前天（8/13）
            Record(
                id = "8",
                type = RecordType.Habit(
                    id = "h4",
                    name = "晨間運動",
                    completed = true
                ),
                time = "08:00",
                date = "8月13日"
            ),
            Record(
                id = "9",
                type = RecordType.Diary(
                    id = "d2",
                    preview = "這個週末想去爬山，要準備一下裝備和食物。天氣預報說會是好天氣..."
                ),
                time = "23:00",
                date = "8月13日"
            ),
            Record(
                id = "10",
                type = RecordType.Expense(
                    id = "e4",
                    amount = -380,
                    category = "娛樂"
                ),
                time = "15:30",
                date = "8月13日"
            )
        )
    }

    private fun createMockActivityMap(): Map<Int, Int> {
        return mapOf(
            1 to 3,
            2 to 1,
            3 to 2,
            5 to 4,
            6 to 2,
            7 to 3,
            8 to 1,
            10 to 5,
            11 to 2,
            12 to 3,
            13 to 4,
            14 to 2,
            15 to 6  // 今天
        )
    }
}
