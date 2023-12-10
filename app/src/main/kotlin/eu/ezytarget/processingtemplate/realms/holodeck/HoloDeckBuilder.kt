package eu.ezytarget.processingtemplate.realms.holodeck

import eu.ezytarget.processingtemplate.realms.Realm
import eu.ezytarget.processingtemplate.realms.RealmBuilder

object HoloDeckBuilder: RealmBuilder {
    override fun build(): Realm = Holodeck()
}