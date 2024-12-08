import requests
import csv
import random

def fetch_top_words():
    url = "https://raw.githubusercontent.com/hermitdave/FrequencyWords/master/content/2018/en/en_50k.txt"
    response = requests.get(url)
    if response.status_code == 200:
        words = response.text.splitlines()
        print(f"Fetched {len(words)} words from the API")  # Debugging statement
        return [word.split()[0] for word in words[:500]]
    else:
        print("Failed to fetch words")
        return []

def categorize_words(words):
    categorized_words = []
    for word in words:
        difficulty = random.randint(1, 100)  # Random difficulty for demonstration
        categorized_words.append((word, difficulty))
    print(f"Categorized {len(categorized_words)} words")  # Debugging statement
    return categorized_words

def export_to_csv(categorized_words, filename="words.csv"):
    with open(filename, mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(["Word", "Difficulty"])
        writer.writerows(categorized_words)
    print(f"Exported {len(categorized_words)} words to {filename}")  # Debugging statement

def main():
    words = fetch_top_words()
    if words:
        categorized_words = categorize_words(words)
        export_to_csv(categorized_words)
        print(f"Exported {len(categorized_words)} words to words.csv")

if __name__ == "__main__":
    main()