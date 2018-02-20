#include<stdio.h>
#include<stdlib.h>

int GetRandom(int min,int max);

int main(void){
	int vid,i;
	int edgenum = 10;
	int size = 500000;
	for(kivid = 0; vid < size; vid++){
		printf("[%d,0,[",vid);
		int rand[size];
		for(i=0;i<edgenum;i++){
			rand[i]=GetRandom(size,size*2-1);
			printf("[%d,0]",rand[i]);
			if(i < edgenum - 1)
			printf(",");
		}
		printf("]]\n");
		for(i=0;i<edgenum;i++){
			printf("[%d,0,[[%d,0]]]\n",rand[i],vid);
		}
	}
return 0;
}

int GetRandom(int min,int max)
{
	return min + (int)(rand()*(max-min+1.0)/(1.0+RAND_MAX));
}
