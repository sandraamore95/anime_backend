package com.appanime.appanime.services;


import com.appanime.appanime.models.Friendship;
import com.appanime.appanime.models.User;

import com.appanime.appanime.payload.request.FriendshipRequest;
import com.appanime.appanime.repository.FriendshipRepository;
import com.appanime.appanime.repository.FriendshipRequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    @Autowired
    FriendshipRepository friendshipRepository;
    @Autowired
    FriendshipRequestRepository friendshipRequestRepository;

    public boolean hasPending(User sender, User receiver) {
        if (sender == null || receiver == null) {
            return false;
        }
        return friendshipRequestRepository.existsBySenderAndReceiver(sender, receiver) ||
                friendshipRequestRepository.existsBySenderAndReceiver(receiver, sender);
    }

    public boolean hasFriendship(User user, User friend) {
        //user es 5
        // friend es 2
        System.out.println(friend.getId());
        return friendshipRepository.existsByUserAndFriend(user, friend);
    }

    public void sendFriendRequest(User sender, User receiver) throws Exception {


        if (sender.getId().equals(receiver.getId())) {
            throw new Exception("No puedes enviarte una solicitud de amistad a ti mismo.");
        }

        // Verificar si ya son amigos en ambas direcciones
        if (hasFriendship(sender, receiver) || hasFriendship(receiver, sender)) {
            throw new Exception("Ya son amigos.");
        }

        // Crear y guardar la solicitud de amistad
        FriendshipRequest friendshipRequest = new FriendshipRequest(sender, receiver);
        friendshipRequestRepository.save(friendshipRequest);
    }


    public void cancelFriendRequest(User sender, User receiver) throws Exception {
        // Obtener la solicitud de amistad entre los dos usuarios
        FriendshipRequest friendRequest = friendshipRequestRepository.findBySenderAndReceiver(sender, receiver)
                .orElseThrow(() -> new Exception("Solicitud de amistad no encontrada entre estos usuarios."));

        // Puedes realizar validaciones adicionales aquí si es necesario

        // Eliminar la solicitud de amistad

        this.friendshipRequestRepository.delete(friendRequest);
    }



    public void acceptFriendRequest(User sender, User receiver) throws Exception {
        // Buscar la solicitud de amistad pendiente entre los dos usuarios
        FriendshipRequest friendRequest = friendshipRequestRepository.findBySenderAndReceiver(sender, receiver)
                .orElseThrow(() -> new Exception("Solicitud de amistad no encontrada entre estos usuarios."));

        // Puedes realizar validaciones adicionales aquí si es necesario

        // Verificar si ya existe una amistad entre los usuarios
        if (!hasFriendship(sender,receiver)) {
            // Crear y guardar la amistad
            Friendship friendship = new Friendship(receiver, sender);
            friendshipRepository.save(friendship);
        }

        // Eliminar la solicitud de amistad
        friendshipRequestRepository.delete(friendRequest);
    }


    public void removeFriendship(User sender, User receiver) {
        friendshipRepository.deleteByUserAndFriend(sender, receiver);
    }

    public FriendshipRequest getFriendshipRequest(User sender, User receiver) {

        Optional<FriendshipRequest> request = friendshipRequestRepository.findBySenderAndReceiver(sender, receiver);
        return request.orElse(null); // Siempre que sea seguro retornar null si no se encuentra
    }


    public boolean isReceiverInPendingRequest(User receiver, User sender) {
        FriendshipRequest friendshipRequest = friendshipRequestRepository
                .findBySenderAndReceiver(sender, receiver)
                .orElse(null);

        return friendshipRequest != null;
    }

    public List<Friendship> getAllFriendships(User user) {
        return friendshipRepository.findAllByUser(user);
    }

    @Transactional
    public void deleteFriendshipsAndRelatedRequestsByUserId(Long userId) {
        // Eliminar amistades donde el usuario es el sender o el receiver
        friendshipRepository.deleteByUserIdOrFriendId(userId, userId);

        // Eliminar solicitudes de amistad donde el usuario es el sender o el receiver
        friendshipRequestRepository.deleteBySenderIdOrReceiverId(userId, userId);
    }


}
