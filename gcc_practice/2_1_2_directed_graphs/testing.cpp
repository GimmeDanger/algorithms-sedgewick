#include <iostream>

#include "digraph.h"
#include "dfs_order.hpp"

using namespace std;

int main ()
{

	cout << "Dfs orders Testing: " << endl;

	digraph G_1 (7);
	G_1.add_edge (0, 1);
	G_1.add_edge (0, 2);
	G_1.add_edge (0, 5);
	G_1.add_edge (1, 4);
	G_1.add_edge (5, 2);
	G_1.add_edge (3, 4);
	G_1.add_edge (3, 2);
	G_1.add_edge (3, 5);
	G_1.add_edge (3, 6);
	G_1.add_edge (6, 0);
	G_1.add_edge (6, 4);

	dfs_order dfso (G_1);
	cout << "Preorder: ";
	for (auto v : dfso.get_preorder ())
		cout << v << " ";
	cout << endl;
	cout << "Postorder: ";
	for (auto v : dfso.get_postorder ())
		cout << v << " ";
	cout << endl;
	cout << "Reverse postorder: ";
	for (auto v : dfso.get_reverse_postorder ())
		cout << v << " ";
	cout << endl;
	cout << endl;

	return 0;
}