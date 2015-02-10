(ns hat.persistance.protocols)

(defprotocol Creatable
  (insert [conn data]))

(defprotocol Browseable
  (all-entities [conn])
  (entity [conn id]))

(defprotocol Updateable
  (swap [conn id data]))

(defprotocol Deleteable
  (delete [conn id]))

(defprotocol Searchable
  (search [conn query attr]))

