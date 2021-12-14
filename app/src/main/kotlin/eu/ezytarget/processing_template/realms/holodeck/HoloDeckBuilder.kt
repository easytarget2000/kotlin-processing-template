package eu.ezytarget.processing_template.realms.holodeck

import eu.ezytarget.processing_template.realms.Realm
import eu.ezytarget.processing_template.realms.RealmBuilder

object HoloDeckBuilder: RealmBuilder {
    override fun build(): Realm = Holodeck()
}