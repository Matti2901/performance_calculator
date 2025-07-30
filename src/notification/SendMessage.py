import os
import json
import requests
import sys

# 🔑 Insert your bot token
BOT_TOKEN = ""
CHAT_ID_FILE = "chat_id.json"

def get_chat_id_from_api():
    url = f"https://api.telegram.org/bot{BOT_TOKEN}/getUpdates"
    response = requests.get(url).json()
    results = response.get("result", [])
    
    for update in reversed(results):
        try:
            return update["message"]["chat"]["id"]
        except KeyError:
            continue
    return None

def load_chat_id():
    if os.path.exists(CHAT_ID_FILE):
        with open(CHAT_ID_FILE) as f:
            return json.load(f)["chat_id"]
    return None

def save_chat_id(chat_id):
    with open(CHAT_ID_FILE, "w") as f:
        json.dump({"chat_id": chat_id}, f)

def send_message(text):
    chat_id = load_chat_id()

    if not chat_id:
        print("🔍 No saved chat_id, trying to retrieve it...")
        chat_id = get_chat_id_from_api()
        if chat_id:
            save_chat_id(chat_id)
            print(f"✅ Chat ID saved: {chat_id}")
        else:
            print("❌ Unable to retrieve chat_id. Have you started the bot on Telegram?")
            return

    url = f"https://api.telegram.org/bot{BOT_TOKEN}/sendMessage"
    payload = {"chat_id": chat_id, "text": text}
    r = requests.post(url, data=payload)

    if r.status_code == 200:
        print("📨 Telegram notification sent!")
    else:
        print(f"❌ Telegram error: {r.status_code} - {r.text}")


# === MAIN ENTRY POINT ===
if __name__ == "__main__":
    if len(sys.argv) == 2:
        text = f"❌ {' '.join(sys.argv[1])}"
    elif len(sys.argv) == 3:
        text = f"✅ {' '.join(sys.argv[1])}"
    else:
        text = "✅ Analysis completed."

    send_message(text)
