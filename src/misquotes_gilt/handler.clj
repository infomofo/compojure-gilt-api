(ns misquotes-gilt.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-http.client :as client]
            [clojure.data.json :as json]))

(def api-key "INSERT YOUR GILT API KEY HERE")

(defn get-json-from-gilt-url[url]
  (json/read-str
    (:body
     (client/get (str url "?apikey=" api-key))
    )
  )
)

(defn get-products-json-string []
  (get-json-from-gilt-url "https://api.gilt.com/v1/sales/men/active.json")
)

(defn get-product-detail-urls []
  (remove nil?
    (flatten
      (map #(get % "products")
         (
            (get-products-json-string)
            "sales"
         )
      )
    )
   )
)

(defn get-random-product-detail-url[]
  (rand-nth (get-product-detail-urls))
)

(def url "https://api.gilt.com/v1/products/1001502742/detail.json")


(defn get-product-image-url-from-json[url]
  ((first
    (last
      (vals
        (
          (get-json-from-gilt-url url)
          "image_urls"
        )
      )
    )
  ) "url")
)

(defn write-image-html[url]
  (str
   "<img src='" url "' style='width:200px'/>")
  )


(defn random-image-url[]
  (get-product-image-url-from-json (get-random-product-detail-url))
)

(defroutes app-routes
  (GET "/product" [] (random-image-url))
  (GET "/" [] (str
               "Would you rather <br/>"
               (write-image-html (random-image-url))
               " OR "
               (write-image-html (random-image-url))
               "<br/>?"
               ))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

