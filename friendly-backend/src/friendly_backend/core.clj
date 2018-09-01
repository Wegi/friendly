(ns friendly-backend.core
  (:require [postal.core :as postal]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

;; TO-DO
;; Save write
;; Save read

(defn load-data!
  [file-path]
  (read-string (slurp file-path)))

(defn save-data!
  [data file-path]
  (spit file-path (prn-str data)))

(defn weighted-uniform-draw
  "Draws a random uniform oject by weight from the collection. `weight-key` denominates which key in the map is refering to the weight."
  [contacts weight-key]
  (let [weights (reductions + (map (keyword weight-key) (vals contacts)))
        total (last weights)
        choices (map vector (keys contacts) weights)]
    (let [choice (rand-int total)]
      (loop [[[contact-name weight] & more] choices]
        (if (< choice weight)
          (get contacts contact-name)
          (recur more))))))

(defn add-friend!
  [friend db-file]
  (let [db (load-data! db-file)]
    (save-data! (assoc db (:name friend) friend) db-file)))

(defn send-reminder
  "Takes a contact and a recipient and sends a reminder."
  [contact recipient]
  (postal/send-message {:host (System/getenv "MAIL_HOST")
                        :user (System/getenv "MAIL_USER")
                        :pass (System/getenv "MAIL_PASS")
                        :tls true}
                       {:from "wegi.pwnz@googlemail.com"
                        :to recipient
                        :subject (format "Meld dich mal bei %s" (:name contact))
                        :body (format "Du hast dich seit %d Tagen nicht mehr bei %s gemeldet.\n\n %s"
                                      (:days-since-contact contact)
                                      (:name contact)
                                      contact)}))

(defn reset-last-seen
  [contacts friend-name]
  (assoc-in contacts [friend-name :days-since-contact] 0))

(defn delete-friend!
  [db-path friend-name]
  (let [db (load-data! db-path)
        new-db (dissoc db friend-name)]
    (save-data! new-db db-path)))

(defn draw-and-send!
  "Draws a friend weighted by last contact in days. Resets the timer to 0 and then sends a reminder-email."
  [db-path weight-key recipient]
  (let [db (load-data! db-path)
        friend (weighted-uniform-draw db weight-key)]
    (save-data! (reset-last-seen db (:name friend)) db-path)
    (send-reminder friend recipient)))

(defn -main
  [database recipient & args]
  (draw-and-send! database :days-since-contact recipient))
