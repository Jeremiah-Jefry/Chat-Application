import pyautogui
import time

# Give the chat windows time to appear
time.sleep(5)

# Take a screenshot
screenshot = pyautogui.screenshot()
screenshot.save("jules-scratch/verification/verification.png")