package com.example.awesomefungame
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.mybudgetapp.LobbyPlayers
import com.example.mybudgetapp.PeoplePlaying
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


open class AppDatabase(
    private val gameActivity: GameActivity) {
    fun retrieveAllDocuments(
        db: FirebaseFirestore,
        lobby: String,
        lobbyPlayers: LobbyPlayers,
        adapter: ArrayAdapter<Any>,
        lvPlayers: ListView
    ) {

        db.collection(lobby).addSnapshotListener(
            object : EventListener<QuerySnapshot?> {
                override fun onEvent(
                    snapshots: QuerySnapshot?,
                    e: FirebaseFirestoreException?
                ) {
                    // Clears any previous data to get the most recent ones
                    lobbyPlayers.players.clear()
                    if (e != null) {
                        println("Listen failed:$e")
                        return
                    }
                    if (snapshots != null) {
                        for (doc in snapshots) {
                            doc.getString("player")?.let {
                                doc.getDouble("score")?.let { it1 -> PeoplePlaying(it, it1) }
                            }?.let { lobbyPlayers.players.add(it) }
                        }
                        gameActivity.setUpScoreData(
                            lobbyPlayers,
                            adapter,
                            lvPlayers)
                    }
                }
            })
    }

    fun addPlayerInLobby(db: FirebaseFirestore, player: Player) {
        val newPlayer = HashMap<String, Any>()
        newPlayer["player"] = player.name
        newPlayer["score"] = player.score
        db.collection(player.lobby)
            .document(player.playerKey)
            .set(newPlayer)
    }

    fun deletePlayerInLobby(db: FirebaseFirestore, player: Player) {
        db.collection(player.lobby)
            .document(player.playerKey)
            .delete()
    }

    fun updateScore(player: Player, db: FirebaseFirestore) {
        db.collection(player.lobby)
            .document(player.playerKey)
            .update("score", player.score)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }
}
