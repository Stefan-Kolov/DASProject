import requests
from bs4 import BeautifulSoup
import pandas as pd
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, timedelta
import os
import time

BASE_URL = "https://www.mse.mk/mk/stats/symbolhistory/"
CSV_FILE = "stock_data.csv"
END_DATE = datetime(2024, 11, 1)


def get_Companies():
    url = "https://www.mse.mk/mk/stats/symbolhistory/ALK"
    response = requests.get(url)
    soup = BeautifulSoup(response.content, 'html.parser')
    symbols = [option.get('value') for option in soup.select("#Code option") if
               option.get('value') and not any(char.isdigit() for char in option.get('value'))]
    print("Retrieved symbols:", symbols)
    return symbols


def checkDatabase(symbol):
    if not os.path.exists(CSV_FILE):
        print(f"{CSV_FILE} not found. Downloading data up to {END_DATE.date()} for {symbol}.")
        return END_DATE - timedelta(days=365 * 10)

    df = pd.read_csv(CSV_FILE)
    if 'date' not in df.columns or 'symbol' not in df.columns:
        print(f"{CSV_FILE} does not contain the required columns. Downloading all data for {symbol}.")
        return END_DATE - timedelta(days=365 * 10)

    df['date'] = pd.to_datetime(df['date'], dayfirst=True, errors='coerce')

    if symbol in df['symbol'].values:
        last_date_str = df[df['symbol'] == symbol]['date'].max()
        print(f"Found existing for {symbol}. Last available date: {last_date_str.date()}")

        if last_date_str >= datetime.now().replace(hour=0, minute=0, second=0,
                                                   microsecond=0):
            print(f"No new data needed for {symbol} since the last date is already today.")
            return None

        return last_date_str if last_date_str < END_DATE else END_DATE
    else:
        print(f"No data found for {symbol}. Downloading up to {END_DATE.date()} for {symbol}.")
        return END_DATE - timedelta(days=365 * 10)


def fetch_data(symbol, from_date, to_date):
    params = {
        "FromDate": from_date.strftime('%Y-%m-%d'),
        "ToDate": to_date.strftime('%Y-%m-%d')
    }
    response = requests.get(f"{BASE_URL}{symbol}", params=params)
    if response.status_code != 200:
        print(f"Error fetching data for {symbol} from {from_date.date()} to {to_date.date()}")
        return None

    soup = BeautifulSoup(response.content, 'html.parser')
    table = soup.find("table")
    if not table:
        return None

    daily_data = []
    for row in table.find_all("tr")[1:]:
        cells = row.find_all("td")
        if len(cells) >= 9:
            last_transaction_raw = cells[1].text.strip()
            try:
                last_transaction = last_transaction_raw.replace(".", "").replace(",", ".")
                last_transaction = "{:,.2f}".format(float(last_transaction))
            except ValueError:
                last_transaction = None

            try:
                max_value = cells[2].text.strip().replace(".", "").replace(",", ".")
                max_value = "{:,.2f}".format(float(max_value))
            except ValueError:
                max_value = None

            try:
                min_value = cells[3].text.strip().replace(".", "").replace(",", ".")
                min_value = "{:,.2f}".format(float(min_value))
            except ValueError:
                min_value = None

            try:
                avg_value = cells[4].text.strip().replace(".", "").replace(",", ".")
                avg_value = "{:,.2f}".format(float(avg_value))
            except ValueError:
                avg_value = None

            record = {
                "symbol": symbol,
                "date": cells[0].text.strip(),
                "Last_Transaction": last_transaction,
                "Max": max_value,
                "Min": min_value,
                "Avg": avg_value,
                "Percent": cells[5].text.strip() if len(cells) > 5 else None,
                "Quantity": cells[6].text.strip() if len(cells) > 6 else None,
                "BEST Profit": cells[7].text.strip() if len(cells) > 7 else None,
                "Total Profit": cells[8].text.strip() if len(cells) > 8 else None,
            }
            daily_data.append(record)
    return daily_data


def fillDatabase(symbol, last_date):
    all_data = []
    if last_date is None:
        return all_data

    if last_date < END_DATE:
        current_start = last_date + timedelta(days=1)
        target_end = END_DATE
    else:
        current_start = last_date + timedelta(days=1)
        target_end = datetime.now()

    date_ranges = []
    while current_start <= target_end:
        current_end = min(current_start + timedelta(days=29), target_end)
        date_ranges.append((symbol, current_start, current_end))
        current_start += timedelta(days=30)

    with ThreadPoolExecutor(max_workers=30) as executor:
        futures = {executor.submit(fetch_data, symbol, from_date, to_date): (from_date, to_date) for
                   symbol, from_date, to_date in date_ranges}
        for future in futures:
            result = future.result()
            if result:
                all_data.extend(result)
    return all_data


def main():
    start_time = time.time()
    symbols = get_Companies()
    all_data = []

    for symbol in symbols:
        last_date = checkDatabase(symbol)
        symbol_data = fillDatabase(symbol, last_date)
        if symbol_data:
            all_data.extend(symbol_data)

    if all_data:
        try:
            existing_df = pd.read_csv(CSV_FILE)
            new_data_df = pd.DataFrame(all_data)
            df = pd.concat([existing_df, new_data_df], ignore_index=True).drop_duplicates()
        except FileNotFoundError:
            df = pd.DataFrame(all_data)

        df["Total Profit"] = pd.to_numeric(df["Total Profit"], errors="coerce")
        df = df[df["Total Profit"] != 0]

        df.loc[df["Total Profit"].isna(), "Total Profit"] = df["BEST Profit"]

        df.to_csv(CSV_FILE, index=False)
        print(f"New data has been saved to {CSV_FILE}")
    else:
        print("No new data retrieved.")

    end_time = time.time()
    minutes, seconds = divmod(end_time - start_time, 60)
    print(f"Execution time: {int(minutes)} minutes and {seconds:.2f} seconds")


if __name__ == "__main__":
    main()
