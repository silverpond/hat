(ns hat.persistance.protocols)

(defprotocol Creatable
  (insert [conn table data]))

(defprotocol Browseable
  (all-entities [conn table])
  (entity [conn table id]))

(defprotocol Updateable
  (swap [conn table id data]))

(defprotocol Deleteable
  (delete [conn table id]))

(defprotocol Searchable
  (search [conn table query attr]))

