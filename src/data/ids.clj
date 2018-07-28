(ns data.ids
  (:require [dtm.util :as util]))

(defmacro defs
  [& bindings]
  {:pre [(even? (count bindings))]}
  `(do
     ~@(for [[sym init] (partition 2 bindings)]
         `(def ~sym ~init))))

(defs
  ;; users
  mp-edu-s-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd000")
  mp-edu-w-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd001")
  mp-liv-s-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd002")
  mp-liv-w-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd003")
  mp-admin-s-id      (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd004")
  mp-admin-w-id      (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd005")
  mp-ops-s-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd006")
  mp-ops-w-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd007")

  odisha-health-s-id (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd008")
  odisha-health-w-id (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd009")
  odisha-admin-s-id  (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd010")
  odisha-admin-w-id  (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd011")
  odisha-ops-s-id    (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd012")
  odisha-ops-w-id    (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd013")

  tn-edu-s-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd014")
  tn-edu-w-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd015")
  tn-health-s-id     (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd016")
  tn-health-w-id     (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd017")
  tn-admin-s-id      (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd018")
  tn-admin-w-id      (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd019")
  tn-ops-s-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd020")
  tn-ops-w-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd021")

  mhr-edu-s-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd022")
  mhr-edu-w-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd023")
  mhr-health-s-id    (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd024")
  mhr-health-w-id    (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd025")
  mhr-admin-s-id     (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd026")
  mhr-admin-w-id     (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd027")
  mhr-ops-s-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd028")
  mhr-ops-w-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd029")

  ;; verticals
  mp-admin-id      (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd061")
  mp-ops-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd062")
  mp-edu-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd063")
  mp-liv-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd064")

  odisha-admin-id  (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd065")
  odisha-ops-id    (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd066")
  odisha-health-id (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd067")

  tn-admin-id      (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd068")
  tn-ops-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd069")
  tn-edu-id        (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd070")
  tn-health-id     (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd071")

  mhr-admin-id     (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd072")
  mhr-ops-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd073")
  mhr-edu-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd074")
  mhr-health-id    (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd075")

  ;; states
  mp-id      (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd090")
  odisha-id  (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd091")
  tn-id      (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd092")
  mhr-id     (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd093")

  ;; projects
  tvs-id   (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd080")
  mrg-id   (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd081")

  ;; activity-templates
  at1-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd100")
  as1-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd101")

  at2-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd200")
  as2-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd201")

  at3-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd300")
  as3-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd301")

  ;; client
  hih-id       (util/str->uuid "99b1b2e5-bcfb-4f3f-b4af-fadfa90dd400"))
