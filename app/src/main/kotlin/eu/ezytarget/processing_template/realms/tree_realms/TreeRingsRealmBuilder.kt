package eu.ezytarget.processing_template.realms.tree_realms

import eu.ezytarget.processing_template.realms.Realm
import eu.ezytarget.processing_template.realms.RealmBuilder

object TreeRingsRealmBuilder : RealmBuilder {

    override fun build(): Realm = TreeRingsRealm()

}