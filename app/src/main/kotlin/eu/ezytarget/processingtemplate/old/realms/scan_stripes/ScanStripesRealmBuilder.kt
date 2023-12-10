package eu.ezytarget.processing_template.realms.scan_stripes

import eu.ezytarget.processing_template.realms.Realm
import eu.ezytarget.processing_template.realms.RealmBuilder

object ScanStripesRealmBuilder : RealmBuilder {
    override fun build(): Realm = ScanStripesRealm()
}