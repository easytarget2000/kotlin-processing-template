package eu.ezytarget.processingtemplate.realms.julia_set

import eu.ezytarget.processingtemplate.realms.Realm
import eu.ezytarget.processingtemplate.realms.RealmBuilder

class JuliaSetRealmBuilder: RealmBuilder {

    var brightness = 1f

    var alpha = 1f

    override fun build(): Realm {
        val juliaSetRealm = JuliaSetRealm()
        juliaSetRealm.brightness = brightness
        juliaSetRealm.alpha = alpha
        return juliaSetRealm
    }
}