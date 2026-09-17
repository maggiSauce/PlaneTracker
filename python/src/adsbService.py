import requests

BASEURL = "https://opendata.adsb.fi/api/v3/"

def getAircraftInArea(lat: str, lon: str, dist: int):
    requestUri = BASEURL + f"lat/{lat}/lon/{lon}/dist/{dist}"

    response = requests.get(requestUri)

    if response.status_code != requests.codes.ok:
        print(f"Failed to get aircraft data: {response.status_code} / {data.get('errorMessage')} \nURI: {requestUri}")
        return False

    data = response.json()
    return data


def main():
    data = getAircraftInArea("53.3147", "-113.5277", 25)
    if data:
        print(data)

if __name__ == "__main__":
    main()
