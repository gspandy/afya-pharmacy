package org.nthdimenzion;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Nthdimenzion
 * Date: 26/10/12
 * Time: 1:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class PasswordHelper {

    static ShaPasswordEncoder encoder = new ShaPasswordEncoder();

    static int arr[];
    static int size[];


    public static void main(String a[]){
        System.out.println("Macula password " + encoder.encodePassword("zpc","zpc"));
        System.out.println("Play games");

        initialise(10);

/*
        union(6,8);
        union(0,1);
        union(4,6);
        union(8,9);
        union(7,6);
        union(6,0);
*/


        weightedUnion(9,3);
        weightedUnion(8,1);
        weightedUnion(9,5);
        weightedUnion(4,7);
        weightedUnion(7,8);
        weightedUnion(0,5);
        weightedUnion(5,6);
        weightedUnion(7,5);
        weightedUnion(6,2);

        System.out.println(Arrays.toString(arr));
    }


    static void initialise(final int n){
        arr = new int[n];
        size = new int[n];
        for (int i = 0; i < size.length; i++) {
            size[i]=1;
        }
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
    }

    static int root(int index){
        while(index != arr[index]){
            index = arr[index];
        }
        return index;

    }

    static void union (int pIndex,int qIndex){
        System.out.println(pIndex + " " + qIndex);
        final int pValue = arr[pIndex];
        for (int i = 0; i < arr.length; i++) {
            if(pValue==arr[i]){
                arr[i]=arr[qIndex];
            }
        }
    }

    static void weightedUnion(int pIndex,int qIndex){
        int pRootIndex = root(pIndex);
        int qRootIndex = root(qIndex);

        if   (size[pRootIndex] < size[qRootIndex]) { arr[pRootIndex] = qRootIndex; size[qRootIndex] += size[pRootIndex]; }
        else                         { arr[qRootIndex] = pRootIndex; size[pRootIndex] += size[qRootIndex]; }

    }

}


