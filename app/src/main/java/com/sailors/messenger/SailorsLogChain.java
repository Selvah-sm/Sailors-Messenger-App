package com.sailors.messenger;

public class SailorsLogChain {

    String TeamToken;

    Node head;
    Node tail;

    static class Node{
        String data;
        String verifyingNextNodeHash;
        Node next;
        Node(String d){
            data = d;
            next = null;
            verifyingNextNodeHash = null;
        }
    }

    // inserting a new node
    public static String insert(SailorsLogChain chain, String data, String verifyingHash,String time, String user){
        String hashedData = HashingAlgorithm.getSHA(user+time+data);
        Node latestNode = new Node(data);

        /*
        if(chain.head == null) {
            chain.head = latestNode;
            chain.tail = latestNode;
            chain.head.verifyingNextNodeHash = hashedData;
            chain.tail.verifyingNextNodeHash = hashedData;
        }
        chain.head.next = latestNode;
        chain.tail = latestNode;
        */

        //Code to traverse from first and create the latest node at last
        if(chain.head == null){
            chain.head = latestNode;
            chain.head.verifyingNextNodeHash = hashedData;
        }
        else{
            Node nodeTraverse = chain.head;
            while(nodeTraverse.next != null){
                nodeTraverse = nodeTraverse.next;
            }
            latestNode.verifyingNextNodeHash = hashedData;
            nodeTraverse.next  = latestNode;
            //nodeTraverse.verifyingNextNodeHash = hashedData;
        }
        printList(chain);
        return hashedData;
    }

    public static void printList(SailorsLogChain list){
        Node currentNode = list.head;
        while(currentNode != null){
            System.out.println(currentNode.data + "  --  "+currentNode.verifyingNextNodeHash);
            currentNode = currentNode.next;
        }
    }





}
