(ns friendly-backend.core
  (:require [postal.core :as postal]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))


(defn load-data!
  [file-path]
  (read-string (slurp file-path)))


(defn save-data!
  [data file-path]
  (spit file-path (prn-str data)))


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
