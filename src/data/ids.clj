(ns data.ids
  (:require [dtm.util :as util]))

(defmacro defs
  [& bindings]
  {:pre [(even? (count bindings))]}
  `(do
     ~@(for [[sym init] (partition 2 bindings)]
         `(def ~sym ~init))))

(defs
  m-a1-id      (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd000")
  m-a2-id      (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd001")
  m-t1-id      (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd002")
  m-t2-id      (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd003")
  m-t3-id      (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd004")
  m-t4-id      (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd005")

  m-sname-id   (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd010")
  m-snum-id    (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd011")
  m-fnd-id     (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd012")
  m-roof-id    (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd013")
  m-hndovr-id  (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd014")

  mt-a2-id     (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd020")
  mt-t1-id     (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd021")
  mt-t2-id     (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd022")
  mt-t3-id     (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd023")
  mt-t4-id     (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd024")

  mt-sname-id  (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd030")
  mt-snum-id   (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd031")
  mt-fnd-id    (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd032")
  mt-roof-id   (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd033")
  mt-hndovr-id (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd034")

  a1-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd040")
  a2-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd041")
  t1-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd042")
  t2-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd043")
  t3-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd044")
  t4-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd045")

  s1-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd050")
  s2-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd051")
  w1-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd052")
  w2-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd053")

  o1-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd060")
  o2-id        (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd061")

  p-id         (util/str->uuid "94b1b2e5-bcfb-4f3f-b4af-fadfa90dd070"))

(defs
  m-a1-id2      (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd000")
  m-a2-id2      (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd001")
  m-t1-id2      (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd002")
  m-t2-id2      (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd003")
  m-t3-id2      (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd004")
  m-t4-id2      (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd005")

  m-sname-id2   (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd010")
  m-snum-id2    (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd011")
  m-fnd-id2     (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd012")
  m-roof-id2    (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd013")
  m-hndovr-id2  (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd014")

  mt-a2-id2     (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd020")
  mt-t1-id2     (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd021")
  mt-t2-id2     (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd022")
  mt-t3-id2     (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd023")
  mt-t4-id2     (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd024")

  mt-sname-id2  (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd030")
  mt-snum-id2   (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd031")
  mt-fnd-id2    (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd032")
  mt-roof-id2   (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd033")
  mt-hndovr-id2 (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd034")

  a1-id2        (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd040")
  a2-id2        (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd041")
  t1-id2        (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd042")
  t2-id2        (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd043")
  t3-id2        (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd044")
  t4-id2        (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd045")

  p-id2         (util/str->uuid "84b1b2e5-bcfb-4f3f-b4af-fadfa90dd070"))
(defs
  pt1-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd100")
  ps1-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd101")

  pt2-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd200")
  ps2-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd201")

  pt3-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd300")
  ps3-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd301"))
