# Coin Tracker

This app has two widget for coins and foreign currencies

## Getting Started

App was written with kotlin

<a href='https://play.google.com/store/apps/details?id=com.omidzamani.cointracker'><img width='200px' alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>


## Resources

The sources that are used for app are:

* [Coin Market Cap](https://api.coinmarketcap.com/v1/) - used for cryptoCoins
* [Coin Tracking](https://min-api.cryptocompare.com/) - for detail page
* [Doviz.com Api](https://www.doviz.com/api/v1/) - used for foreign currencies based on Turkish Lira

### Installing

For running the app in your android studio you need to set some variable in your gradle.properties


```
key=example.keystore
key_alias=example
store_password=********
key_password=******

#fabric
fabric_key=********************************
```


## Built With

* [Anko](https://github.com/Kotlin/anko) - used for alerts and Dialogs
* [Fabric](https://fabric.io/) - Dependency Management
* [okhttp3](https://github.com/square/okhttp) - Used to generate RSS Feeds
* [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - used in detailed activity for coins 
* [Gson](https://github.com/google/gson) - general using



## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
