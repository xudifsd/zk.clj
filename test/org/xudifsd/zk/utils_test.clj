(ns org.xudifsd.zk.utils-test
  (:require [clojure.test :refer :all])
  (:use org.xudifsd.zk.utils))

(deftest ignore-exceptions-test
  (testing "test not throw specified exception"
    (is (= 'exception-catched
           (ignore-exceptions [Exception]
                   (throw (Exception. "some info"))))))

  (testing "test throw exceptions not specified"
    (is (thrown? Exception
        (ignore-exceptions [RuntimeException]
                (throw (Exception. "some info"))))))

  (testing "test specified multi exceptions"
    (is (= 'exception-catched
           (ignore-exceptions [Exception RuntimeException]
                   (/ 1 0))))))
