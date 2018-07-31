(ns friendly-backend.core
  (:require [postal.core :as postal]))

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
  []
  (foo "message"))
