;; rewriting https://clojuredocs.org/clojure.core/lazy-seq#example-542692d3c026201cdc326ff1
;; Q: why the stack overflow?
(defn lazy-trialdivision
  ([] (lazy-trialdivision (iterate inc 2))) ; lazy list 2,3,4,5,....
  ([[prime & nums]] ;destructuring list of numbers, 1st is a prime
   (cons prime
         (lazy-seq
          (lazy-trialdivision (filter #(not= 0 (mod % prime)) nums))))))

;; simplified from http://stackoverflow.com/questions/960980/fast-prime-number-generation-in-clojure
;; removed optimization for readability
(defn java-sieve [n]
  "returns a BitSet with bits set for each prime up to n"
  (let [bs (java.util.BitSet. n)]
    (.flip bs 2 n)
    (doseq [p (range 2 (Math/sqrt n))]
      (if (.get bs p)
        (doseq [q (range (* p p) n p)] (.clear bs q))))
    bs))

;; Taken from clojure.contrib.lazy-seqs
; wheel-sieve cannot be written efficiently as a function, because
; it needs to look back on the whole sequence.
(def wheel-sieve
  (concat
   [2 3 5 7]
   (lazy-seq
    (let [primes-from
          (fn primes-from [n [f & r]]
            (if (some #(zero? (rem n %))
                      (take-while #(<= (* % %) n) wheel-sieve))
              (recur (+ n f) r)
              (lazy-seq (cons n (primes-from (+ n f) r)))))
          wheel (cycle [2 4 2 4 6 2 6 4 2 4 6 6 2 6  4  2
                        6 4 6 8 4 2 4 2 4 8 6 4 6 2  4  6
                        2 6 6 4 2 4 6 2 6 4 2 4 2 10 2 10])]
      (primes-from 11 wheel)))))
