(ns friendly-backend.core
  (:require [postal.core :as postal]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [r (io/reader source)]
      (edn/read (java.io.PushbackReader. r)))

    (catch java.io.IOException e
      (printf "Couldn't open '%s': %s\n" source (.getMessage e)))
    (catch RuntimeException e
      (printf "Error parsing edn file '%s': %s\n" source (.getMessage e)))))

(defn weighted-uniform-draw
  "Draws a random uniform oject by weight from the collection. `weight-key` denominates which key in the map is refering to the weight."
  [contacts weight-key]
  (let [weights (reductions + (map (keyword weight-key) contacts))
        total (last weights)
        choices (map vector contacts weights)]
    (let [choice (rand-int total)]
      (loop [[[contact weight] & more] choices]
        (if (< choice weight)
          contact
          (recur more))))))

(defn foo
  "I don't do a whole lot."
  [x]
  (postal/send-message {:host (System/getenv "MAIL_HOST")
                        :user (System/getenv "MAIL_USER")
                        :pass (System/getenv "MAIL_PASS")
                        :tls true}
                       {:from "wegi.pwnz@googlemail.com"
                        :to "alexander@schneider.gg"
                        :subject "Test friends"
                        :body "Heyaa contact your friends."}))

(defn -main
  [& args]
  (print "Hello friends"))
