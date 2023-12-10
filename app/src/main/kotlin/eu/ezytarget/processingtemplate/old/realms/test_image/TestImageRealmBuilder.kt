package eu.ezytarget.processing_template.realms.test_image

import eu.ezytarget.processing_template.realms.Realm
import eu.ezytarget.processing_template.realms.RealmBuilder

object TestImageRealmBuilder: RealmBuilder {
    override fun build(): Realm = TestImageRealm()
}