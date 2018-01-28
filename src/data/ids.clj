(ns data.ids
  (:require [dtm.util :as util]))

(defmacro defs
  [& bindings]
  {:pre [(even? (count bindings))]}
  `(do
     ~@(for [[sym init] (partition 2 bindings)]
         `(def ~sym ~init))))

(defs
  m-a1-id      (util/str->uuid "10b1b2e5-bcfb-4f3f-b4af-fadfa90dd000")
  m-a2-id      (util/str->uuid "10b1b2e5-bcfb-4f3f-b4af-fadfa90dd001")
  m-t1-id      (util/str->uuid "10b1b2e5-bcfb-4f3f-b4af-fadfa90dd002")
  m-t2-id      (util/str->uuid "10b1b2e5-bcfb-4f3f-b4af-fadfa90dd003")
  m-t3-id      (util/str->uuid "10b1b2e5-bcfb-4f3f-b4af-fadfa90dd004")
  m-t4-id      (util/str->uuid "10b1b2e5-bcfb-4f3f-b4af-fadfa90dd005")

  m-sname-id           (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd010")
  m-snum-id            (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd011")
  m-fnd-id             (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd012")
  m-roof-photo-id      (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd013")
  m-roof-height-id     (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd014")
  m-roof-pillars-id    (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd015")
  m-roof-location-id   (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd016")
  m-roof-date-id       (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd017")
  m-roof-contractor-id (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd018")
  m-hndovr-photo-id    (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd019")
  m-hndovr-location-id (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd020")
  m-hndovr-name-id     (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd021")
  m-hndovr-date-id     (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd022")
  m-hndovr-area-id     (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd023")
  m-hndovr-output-id   (util/str->uuid "11b1b2e5-bcfb-4f3f-b4af-fadfa90dd024")

  mt-a2-id     (util/str->uuid "12b1b2e5-bcfb-4f3f-b4af-fadfa90dd020")
  mt-t1-id     (util/str->uuid "12b1b2e5-bcfb-4f3f-b4af-fadfa90dd021")
  mt-t2-id     (util/str->uuid "12b1b2e5-bcfb-4f3f-b4af-fadfa90dd022")
  mt-t3-id     (util/str->uuid "12b1b2e5-bcfb-4f3f-b4af-fadfa90dd023")
  mt-t4-id     (util/str->uuid "12b1b2e5-bcfb-4f3f-b4af-fadfa90dd024")

  mt-sname-id           (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd010")
  mt-snum-id            (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd011")
  mt-fnd-id             (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd012")
  mt-roof-photo-id      (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd013")
  mt-roof-height-id     (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd014")
  mt-roof-pillars-id    (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd015")
  mt-roof-location-id   (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd016")
  mt-roof-date-id       (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd017")
  mt-roof-contractor-id (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd018")
  mt-hndovr-photo-id    (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd019")
  mt-hndovr-location-id (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd020")
  mt-hndovr-name-id     (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd021")
  mt-hndovr-date-id     (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd022")
  mt-hndovr-area-id     (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd023")
  mt-hndovr-output-id   (util/str->uuid "13b1b2e5-bcfb-4f3f-b4af-fadfa90dd024")

  a1-id        (util/str->uuid "14b1b2e5-bcfb-4f3f-b4af-fadfa90dd040")
  a2-id        (util/str->uuid "14b1b2e5-bcfb-4f3f-b4af-fadfa90dd041")
  t1-id        (util/str->uuid "14b1b2e5-bcfb-4f3f-b4af-fadfa90dd042")
  t2-id        (util/str->uuid "14b1b2e5-bcfb-4f3f-b4af-fadfa90dd043")
  t3-id        (util/str->uuid "14b1b2e5-bcfb-4f3f-b4af-fadfa90dd044")
  t4-id        (util/str->uuid "14b1b2e5-bcfb-4f3f-b4af-fadfa90dd045")

  p-id         (util/str->uuid "15b1b2e5-bcfb-4f3f-b4af-fadfa90dd070"))

(defs
  m-a1-id2      (util/str->uuid "60b1b2e5-bcfb-4f3f-b4af-fadfa90dd000")
  m-a2-id2      (util/str->uuid "60b1b2e5-bcfb-4f3f-b4af-fadfa90dd001")
  m-t1-id2      (util/str->uuid "60b1b2e5-bcfb-4f3f-b4af-fadfa90dd002")
  m-t2-id2      (util/str->uuid "60b1b2e5-bcfb-4f3f-b4af-fadfa90dd003")
  m-t3-id2      (util/str->uuid "60b1b2e5-bcfb-4f3f-b4af-fadfa90dd004")
  m-t4-id2      (util/str->uuid "60b1b2e5-bcfb-4f3f-b4af-fadfa90dd005")

  m-sname-id2           (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd010")
  m-snum-id2            (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd011")
  m-fnd-id2             (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd012")
  m-roof-photo-id2      (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd013")
  m-roof-height-id2     (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd014")
  m-roof-pillars-id2    (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd015")
  m-roof-location-id2   (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd016")
  m-roof-date-id2       (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd017")
  m-roof-contractor-id2 (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd018")
  m-hndovr-photo-id2    (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd019")
  m-hndovr-location-id2 (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd020")
  m-hndovr-name-id2     (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd021")
  m-hndovr-date-id2     (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd022")
  m-hndovr-area-id2     (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd023")
  m-hndovr-output-id2   (util/str->uuid "61b1b2e5-bcfb-4f3f-b4af-fadfa90dd024")

  mt-a2-id2     (util/str->uuid "62b1b2e5-bcfb-4f3f-b4af-fadfa90dd020")
  mt-t1-id2     (util/str->uuid "62b1b2e5-bcfb-4f3f-b4af-fadfa90dd021")
  mt-t2-id2     (util/str->uuid "62b1b2e5-bcfb-4f3f-b4af-fadfa90dd022")
  mt-t3-id2     (util/str->uuid "62b1b2e5-bcfb-4f3f-b4af-fadfa90dd023")
  mt-t4-id2     (util/str->uuid "62b1b2e5-bcfb-4f3f-b4af-fadfa90dd024")

  mt-sname-id2           (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd010")
  mt-snum-id2            (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd011")
  mt-fnd-id2             (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd012")
  mt-roof-photo-id2      (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd013")
  mt-roof-height-id2     (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd014")
  mt-roof-pillars-id2    (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd015")
  mt-roof-location-id2   (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd016")
  mt-roof-date-id2       (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd017")
  mt-roof-contractor-id2 (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd018")
  mt-hndovr-photo-id2    (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd019")
  mt-hndovr-location-id2 (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd020")
  mt-hndovr-name-id2     (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd021")
  mt-hndovr-date-id2     (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd022")
  mt-hndovr-area-id2     (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd023")
  mt-hndovr-output-id2   (util/str->uuid "63b1b2e5-bcfb-4f3f-b4af-fadfa90dd024")

  a1-id2        (util/str->uuid "64b1b2e5-bcfb-4f3f-b4af-fadfa90dd040")
  a2-id2        (util/str->uuid "64b1b2e5-bcfb-4f3f-b4af-fadfa90dd041")
  t1-id2        (util/str->uuid "64b1b2e5-bcfb-4f3f-b4af-fadfa90dd042")
  t2-id2        (util/str->uuid "64b1b2e5-bcfb-4f3f-b4af-fadfa90dd043")
  t3-id2        (util/str->uuid "64b1b2e5-bcfb-4f3f-b4af-fadfa90dd044")
  t4-id2        (util/str->uuid "64b1b2e5-bcfb-4f3f-b4af-fadfa90dd045")

  p-id2         (util/str->uuid "65b1b2e5-bcfb-4f3f-b4af-fadfa90dd070"))

(defs
  s1-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd050")
  s2-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd051")
  w1-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd052")
  w2-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd053")

  o1-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd060")
  o2-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd061")

  pt1-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd100")
  ps1-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd101")

  pt2-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd200")
  ps2-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd201")

  pt3-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd300")
  ps3-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd301"))
