package eu.ezytarget.processingtemplate.realms.test_image

import eu.ezytarget.processingtemplate.realms.Realm
import eu.ezytarget.processingtemplate.realms.RealmBuilder

object TestImageRealmBuilder: RealmBuilder {
    override fun build(): Realm = TestImageRealm()
}