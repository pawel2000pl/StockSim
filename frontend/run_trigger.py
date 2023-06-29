import requests

def download_website(adres_url):
    try:
        response = requests.get(adres_url)
        response.raise_for_status()  
        content = response.text
        return content
    except requests.exceptions.RequestException as e:
        print("Error, cannot download the page: ", e)
