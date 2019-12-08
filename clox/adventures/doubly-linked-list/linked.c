#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/* List Internals */
typedef struct Node {
	struct Node* prev;
	struct Node* next;
	char value[];
} Node;

Node* createNode(char* newValue) {
	Node* retNode = malloc(sizeof(*retNode) + sizeof(char [strlen(newValue)]));
	strcpy(retNode->value, newValue);
	return retNode;
}

Node* getTail(Node* head) {
	if (head == NULL) {
		return head;
	}
	Node* curr = head;
	while (curr->next != NULL) {
		curr = curr->next;
	}
	return curr;
}

int listLength(Node* head) {
	int ret = 0;
	while (head != NULL) {
		head = head->next;
		ret++;
	}
	return ret;
}

/* List Actions */
Node* append(Node* head, char* newValue) {
	Node* tail = getTail(head);
	if (tail == NULL) {
		return createNode(newValue);
	} else {
		Node* newNode = createNode(newValue);
		newNode->prev = tail;
		tail->next = newNode;
		return head;
	}
}

Node* insert(Node* head, char* newValue, int index) {
	Node* curr = head;
	if (head == NULL) {
		return createNode(newValue);
	}
	if (listLength(head) <= index) {
		return append(head, newValue);
	}
	if (index == 0) {
		// Create a new head at the beginning of the list
		Node* newHead = createNode(newValue);
		newHead->next = head;
		head->prev = newHead;
		return newHead;
	}
	// walk to insertion point (which is the item just before the index)
	for (int i = 0; i < index - 1; i++) {
		curr = curr->next;
	}
	// insert
	Node* newNode = createNode(newValue);
	newNode->prev = curr;
	newNode->next = curr->next;
	// update next (we know there is one, as we checked the length earlier)
	curr->next->prev = newNode;
	// update curr
	curr->next = newNode;
	return head;
}

Node* prepend(Node* head, char* newValue) {
	return insert(head, newValue, 0);
}

Node* delete(Node* head, int index) {
	if (index >= 0 && index < listLength(head)) {
		if (index == 0) {
			Node* ret = head->next;
			free(head);
			if (ret != NULL) {
				ret->prev = NULL;
			}
			return ret;
		}
		// walk to the deletion point
		Node* curr = head;
		for (int i = 0; i < index; i++) {
			curr = curr->next;
		}
		// delete
		curr->prev->next = curr->next;
		if (curr->next != NULL) {
			curr->next->prev = curr->prev;
		}
		free(curr);
		return head;
	}
	// THIS IS VERY DANGEROUS
	return NULL;
}

Node* find(Node* head, char* sought) {
	for (Node* curr = head; curr != NULL; curr = curr->next) {
		if (strcmp(curr->value, sought) == 0) {
			return curr;
		}
	}
	return NULL;
}

void printList(Node* head) {
	Node* curr = head;
	printf("[");
	while (curr != NULL) {
		printf("\"%s\"", curr->value);
		if (curr->next != NULL) {
			printf(", ");
		}
		curr = curr->next;
	}
	printf("]\n");
}

void printListReversed(Node* head) {
	Node* curr = getTail(head);
	printf("[");
	while (curr != NULL) {
		printf("\"%s\"", curr->value);
		if (curr->prev != NULL) {
			printf(", ");
		}
		curr = curr->prev;
	}
	printf("]\n");
}

// Driver
int main() {

	// Creation
	Node* list = createNode("Hello");
	list = append(list, "There");
	list = append(list, "Friend!");

	// Printing
	printf("%s\n", "Printing forward");
	printList(list);
	printf("%s\n", "Printing reversed");
	printListReversed(list);

	// Insertion
	printf("%s\n", "Inserting at index 20");
	list = insert(list, "The newest node", 20);
	printf("%s", "-->");
	printList(list);
	printf("%s", "<--");
	printListReversed(list);
	printf("%s\n", "Inserting at index 1");
	list = insert(list, "The newest node", 1);
	printf("%s", "-->");
	printList(list);
	printf("%s", "<--");
	printListReversed(list);
	printf("%s\n", "Inserting at index 0");
	list = insert(list, "The newest node", 0);
	printf("%s", "-->");
	printList(list);
	printf("%s", "<--");
	printListReversed(list);

	// Search
	printf("%s\n", "Finding \"Tree\"");
	Node* found = find(list, "Tree");
	if (found != NULL) {
		printf("%s\n", "Found \"Tree\"!");
	} else {
		printf("%s\n", "\"Tree\" not found.");
	}
	printf("%s\n", "Finding \"There\"");
	found = find(list, "There");
	if (found != NULL) {
		printf("%s\n", "Found \"There\"!");
	} else {
		printf("%s\n", "\"There\" not found.");
	}

	// Deletion
	printf("%s\n", "Deleting at index 2");
	list = delete(list, 2);
	printf("%s", "-->");
	printList(list);
	printf("%s", "<--");
	printListReversed(list);
	printf("%s\n", "Deleting at index 0");
	list = delete(list, 0);
	printf("%s", "-->");
	printList(list);
	printf("%s", "<--");
	printListReversed(list);
	printf("%s\n", "Deleting at index 3");
	list = delete(list, 3);
	printf("%s", "-->");
	printList(list);
	printf("%s", "<--");
	printListReversed(list);
	printf("%s\n", "Deleting at index 0");
	list = delete(list, 0);
	printf("%s", "-->");
	printList(list);
	printf("%s", "<--");
	printListReversed(list);
	printf("%s\n", "Deleting at index 0");
	list = delete(list, 0);
	printf("%s", "-->");
	printList(list);
	printf("%s", "<--");
	printListReversed(list);
	printf("%s\n", "Deleting at index 0");
	list = delete(list, 0);
	printf("%s", "-->");
	printList(list);
	printf("%s", "<--");
	printListReversed(list);

	return 0;
}