(ns election-guide.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [election-guide.core-test]))

(enable-console-print!)

(doo-tests 'election-guide.core-test)
