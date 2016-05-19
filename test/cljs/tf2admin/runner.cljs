(ns tf2admin.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [tf2admin.core-test]))

(doo-tests 'tf2admin.core-test)
