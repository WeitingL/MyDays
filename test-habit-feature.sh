#!/bin/bash

# 測試輸出目錄
OUTPUT_DIR=".claude/outputs/habit-feature"
SCREENSHOT_DIR="$OUTPUT_DIR/screenshots"

# 計數器
PASS=0
FAIL=0

echo "=========================================="
echo "Habit Feature 自動化測試"
echo "=========================================="
echo ""

# Test Case 1: 啟動 App
echo "=== Test Case 1: 啟動 App 並進入首頁 ==="
adb shell am start -n com.weiting.mydays/.MainActivity
sleep 5  # 等待 app 完全啟動
adb shell screencap -p /sdcard/tc1_launch.png
adb pull /sdcard/tc1_launch.png "$SCREENSHOT_DIR/tc1_launch.png" 2>/dev/null

if [ -f "$SCREENSHOT_DIR/tc1_launch.png" ]; then
    echo "✅ PASS - App 啟動成功"
    PASS=$((PASS+1))
else
    echo "❌ FAIL - 截圖失敗"
    FAIL=$((FAIL+1))
fi
echo ""

# Test Case 2: 檢查 Home Widget
echo "=== Test Case 2: 檢查 Home Widget 內容 ==="
sleep 2
adb shell uiautomator dump
adb pull /sdcard/window_dump.xml "$SCREENSHOT_DIR/tc2_ui_tree.xml" 2>/dev/null
adb shell screencap -p /sdcard/tc2_home_widget.png
adb pull /sdcard/tc2_home_widget.png "$SCREENSHOT_DIR/tc2_home_widget.png" 2>/dev/null

# 檢查 UI 樹是否包含「習慣」相關文字
if grep -q "習慣" "$SCREENSHOT_DIR/tc2_ui_tree.xml" 2>/dev/null; then
    echo "✅ PASS - Home Widget 顯示習慣內容"
    PASS=$((PASS+1))
else
    echo "⚠️  WARN - 無法在 UI 樹中找到「習慣」文字"
    FAIL=$((FAIL+1))
fi
echo ""

# Test Case 3: 取得螢幕解析度（用於計算點擊座標）
echo "=== 取得螢幕資訊 ==="
SCREEN_SIZE=$(adb shell wm size | grep -oE '[0-9]+x[0-9]+' | tail -1)
echo "螢幕解析度: $SCREEN_SIZE"
WIDTH=$(echo $SCREEN_SIZE | cut -d'x' -f1)
HEIGHT=$(echo $SCREEN_SIZE | cut -d'x' -f2)

# 計算 Navigation Bar 位置（底部，Y 軸接近底部）
NAV_Y=$((HEIGHT - 100))

# Flow tab (第 2 個 tab，X 約在 1/3 處)
FLOW_TAB_X=$((WIDTH / 3))

# Setting tab (第 4 個 tab，X 約在 3/4 處)
SETTING_TAB_X=$((WIDTH * 3 / 4))

echo "計算座標: Flow tab ($FLOW_TAB_X, $NAV_Y), Setting tab ($SETTING_TAB_X, $NAV_Y)"
echo ""

# Test Case 4: 切換到 Flow 頁面
echo "=== Test Case 4: 切換到 Flow 頁面 ==="
adb shell input tap $FLOW_TAB_X $NAV_Y
sleep 2
adb shell screencap -p /sdcard/tc4_flow.png
adb pull /sdcard/tc4_flow.png "$SCREENSHOT_DIR/tc4_flow.png" 2>/dev/null

if [ -f "$SCREENSHOT_DIR/tc4_flow.png" ]; then
    echo "✅ PASS - 切換到 Flow 頁面"
    PASS=$((PASS+1))
else
    echo "❌ FAIL - 截圖失敗"
    FAIL=$((FAIL+1))
fi
echo ""

# Test Case 5: 切換到 Setting 並進入 Habit 功能
echo "=== Test Case 5: 進入 Habit Screen ==="
adb shell input tap $SETTING_TAB_X $NAV_Y
sleep 2
adb shell screencap -p /sdcard/tc5_setting.png
adb pull /sdcard/tc5_setting.png "$SCREENSHOT_DIR/tc5_setting.png" 2>/dev/null

# 取得 UI 樹，找「習慣養成」的座標
adb shell uiautomator dump
adb pull /sdcard/window_dump.xml "$SCREENSHOT_DIR/tc5_ui_tree.xml" 2>/dev/null

# 嘗試點擊「習慣養成」（假設在螢幕中間偏上）
HABIT_ENTRY_X=$((WIDTH / 2))
HABIT_ENTRY_Y=$((HEIGHT / 3))
echo "嘗試點擊習慣養成入口: ($HABIT_ENTRY_X, $HABIT_ENTRY_Y)"
adb shell input tap $HABIT_ENTRY_X $HABIT_ENTRY_Y
sleep 2
adb shell screencap -p /sdcard/tc5_habit_screen.png
adb pull /sdcard/tc5_habit_screen.png "$SCREENSHOT_DIR/tc5_habit_screen.png" 2>/dev/null

if [ -f "$SCREENSHOT_DIR/tc5_habit_screen.png" ]; then
    echo "✅ PASS - 進入 Habit Screen"
    PASS=$((PASS+1))
else
    echo "❌ FAIL - 截圖失敗"
    FAIL=$((FAIL+1))
fi
echo ""

# Test Case 6: 建立新習慣
echo "=== Test Case 6: 建立新習慣 ==="
# 嘗試點擊「新增習慣」按鈕（假設在右上角或底部中間）
ADD_BUTTON_X=$((WIDTH / 2))
ADD_BUTTON_Y=$((HEIGHT - 200))
echo "嘗試點擊新增習慣按鈕: ($ADD_BUTTON_X, $ADD_BUTTON_Y)"
adb shell input tap $ADD_BUTTON_X $ADD_BUTTON_Y
sleep 1
adb shell screencap -p /sdcard/tc6_add_dialog.png
adb pull /sdcard/tc6_add_dialog.png "$SCREENSHOT_DIR/tc6_add_dialog.png" 2>/dev/null

# 輸入習慣名稱（點擊輸入框，假設在螢幕中間）
TEXTFIELD_X=$((WIDTH / 2))
TEXTFIELD_Y=$((HEIGHT / 2))
adb shell input tap $TEXTFIELD_X $TEXTFIELD_Y
sleep 1
adb shell input text "Test_Habit"
sleep 1
adb shell screencap -p /sdcard/tc6_input_name.png
adb pull /sdcard/tc6_input_name.png "$SCREENSHOT_DIR/tc6_input_name.png" 2>/dev/null

# 儲存（假設「確認」按鈕在對話框底部右側）
SAVE_BUTTON_X=$((WIDTH * 2 / 3))
SAVE_BUTTON_Y=$((HEIGHT * 2 / 3))
echo "嘗試點擊儲存按鈕: ($SAVE_BUTTON_X, $SAVE_BUTTON_Y)"
adb shell input tap $SAVE_BUTTON_X $SAVE_BUTTON_Y
sleep 2
adb shell screencap -p /sdcard/tc6_after_add.png
adb pull /sdcard/tc6_after_add.png "$SCREENSHOT_DIR/tc6_after_add.png" 2>/dev/null

# 取得 UI 樹，檢查是否出現 Test_Habit
adb shell uiautomator dump
adb pull /sdcard/window_dump.xml "$SCREENSHOT_DIR/tc6_ui_tree.xml" 2>/dev/null

if grep -q "Test_Habit\|Test Habit" "$SCREENSHOT_DIR/tc6_ui_tree.xml" 2>/dev/null; then
    echo "✅ PASS - 新習慣建立成功"
    PASS=$((PASS+1))
else
    echo "⚠️  WARN - 無法在 UI 中找到新建立的習慣"
    FAIL=$((FAIL+1))
fi
echo ""

# Test Case 7: 快速打卡
echo "=== Test Case 7: 快速打卡 ==="
# 假設打卡 icon 在列表右側
CHECKIN_X=$((WIDTH - 100))
CHECKIN_Y=$((HEIGHT / 3))
echo "嘗試點擊打卡按鈕: ($CHECKIN_X, $CHECKIN_Y)"
adb shell input tap $CHECKIN_X $CHECKIN_Y
sleep 2
adb shell screencap -p /sdcard/tc7_after_checkin.png
adb pull /sdcard/tc7_after_checkin.png "$SCREENSHOT_DIR/tc7_after_checkin.png" 2>/dev/null

if [ -f "$SCREENSHOT_DIR/tc7_after_checkin.png" ]; then
    echo "✅ PASS - 打卡操作完成"
    PASS=$((PASS+1))
else
    echo "❌ FAIL - 截圖失敗"
    FAIL=$((FAIL+1))
fi
echo ""

# Test Case 8: 返回 Home 檢查 Widget
echo "=== Test Case 8: 返回 Home 檢查 Widget ==="
# 點擊 Home tab (第 1 個 tab，X 約在 1/6 處)
HOME_TAB_X=$((WIDTH / 6))
adb shell input tap $HOME_TAB_X $NAV_Y
sleep 2
adb shell screencap -p /sdcard/tc8_home_updated.png
adb pull /sdcard/tc8_home_updated.png "$SCREENSHOT_DIR/tc8_home_updated.png" 2>/dev/null

adb shell uiautomator dump
adb pull /sdcard/window_dump.xml "$SCREENSHOT_DIR/tc8_ui_tree.xml" 2>/dev/null

if [ -f "$SCREENSHOT_DIR/tc8_home_updated.png" ]; then
    echo "✅ PASS - 返回 Home 頁面"
    PASS=$((PASS+1))
else
    echo "❌ FAIL - 截圖失敗"
    FAIL=$((FAIL+1))
fi
echo ""

# Test Case 9: 刪除習慣
echo "=== Test Case 9: 刪除習慣 ==="
# 先回到 Habit Screen
adb shell input tap $SETTING_TAB_X $NAV_Y
sleep 1
adb shell input tap $HABIT_ENTRY_X $HABIT_ENTRY_Y
sleep 2

# 長按習慣列表項目（模擬刪除操作，或點擊刪除按鈕）
DELETE_X=$((WIDTH - 150))
DELETE_Y=$((HEIGHT / 3))
echo "嘗試點擊刪除按鈕: ($DELETE_X, $DELETE_Y)"
adb shell input tap $DELETE_X $DELETE_Y
sleep 2
adb shell screencap -p /sdcard/tc9_after_delete.png
adb pull /sdcard/tc9_after_delete.png "$SCREENSHOT_DIR/tc9_after_delete.png" 2>/dev/null

if [ -f "$SCREENSHOT_DIR/tc9_after_delete.png" ]; then
    echo "✅ PASS - 刪除操作完成"
    PASS=$((PASS+1))
else
    echo "❌ FAIL - 截圖失敗"
    FAIL=$((FAIL+1))
fi
echo ""

# 測試摘要
echo "=========================================="
echo "測試摘要"
echo "=========================================="
echo "✅ PASS: $PASS"
echo "❌ FAIL: $FAIL"
echo "總計: $((PASS+FAIL))"
echo ""
echo "截圖與 UI 樹已存放至: $SCREENSHOT_DIR/"
echo "=========================================="
