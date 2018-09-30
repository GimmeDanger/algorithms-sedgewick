#include <bits/stdc++.h>
using namespace std;

#define dbg 1

int letter_to_index (char letter)
{
  switch (letter)
    {
    case 'A': return 0; break;
    case 'C': return 1; break;
    case 'G': return 2; break;
    case 'T': return 3; break;
    case '$': return 4; break;
    default: assert (false); return -1;
    }
}

template <int R>
struct suffix_tree_node
{
  int starting_pos = 0;
  int length = 0;
  bool is_leaf = false;
  std::array<suffix_tree_node<R> *, R> next;

  // Methods
  void reset (int _starting_pos, int _length, int _is_word)
  {
    starting_pos = _starting_pos;
    length = _length;
    is_leaf = _is_word;
  }

  // Constructors
  suffix_tree_node (int _starting_pos = 0, int _length = 0, bool _is_leaf = false)
    : starting_pos (_starting_pos), length (_length), is_leaf (_is_leaf)
  {
    for (int c = 0; c < R; c++)
      next[c] = nullptr;
  }
};

template <int R>
class suffix_tree
{
  typedef suffix_tree_node<R> node;
  const std::string m_pattern;
  node *root = nullptr;

  int lcp (const int x_starting_pos, const int x_length, const int y_starting_pos, const int y_length)
  {
    int i = 0;
    for (; i < x_length && i < y_length; i++)
      if (m_pattern[x_starting_pos + i] != m_pattern[y_starting_pos + i])
        break;
    return i;
  }

  void split (node *x, const int _lcp)
  {
    // Create a new node
    node *split = new node (x->starting_pos + _lcp, x->length - _lcp, true /*is_leaf*/);
    for (int c = 0; c < R; c++)
      swap (split->next[c], x->next[c]);

    // Modify the current node and make a link with the new node
    x->reset (x->starting_pos, _lcp, false /*is_leaf*/);
    int c = letter_to_index (m_pattern.at (split->starting_pos));
    x->next[c] = split;
  }

  node *put (node *x, const int starting_pos, const int length)
  {
    if (x == nullptr)
      {
        x = new node (starting_pos, length, true /*is_leaf*/);
        return x;
      }

    int _lcp = lcp (starting_pos, length, x->starting_pos, x->length);
    if (_lcp > 0 && x->length > 1) split (x, _lcp);

    int c = letter_to_index (m_pattern.at (starting_pos + _lcp));
    x->next[c] = put (x->next[c], starting_pos + _lcp, length - _lcp);
    return x;
  }

  void collect_nodes_by_postorder (node *x, std::vector<std::string> &results)
  {
    if (!x) return;
    results.emplace_back (m_pattern.substr (x->starting_pos, x->length));
    for (int c = 0; c < R; c++)
      collect_nodes_by_postorder (x->next[c], results);
  }

  void free_by_postorder (node *x)
  {
      if (!x) return;
      for (int c = 0; c < R; c++)
        free_by_postorder (x->next[c]);
      delete (x);
  }

public:
  suffix_tree (const std::string &pattern) : m_pattern (pattern)
  {
    if (R < 1 || R > 256)
      throw std::runtime_error ("Error: Radix (R) must be in range [1, 256].");

    const int pat_size = m_pattern.size();
    for (auto &l : m_pattern)
      {
        if (letter_to_index (l) >= R)
          throw std::runtime_error (
              "Error: Radix (R) is less than a number"
              "of unique letters in a given  pattern.");
      }

    root = new node ();
    for (int i = pat_size - 1; i >= 0; i--)
      root = put (root, i, pat_size - i);
  }

  std::vector<std::string> collect_nodes ()
  {
    std::vector<std::string> results;
    collect_nodes_by_postorder (root, results);
    return results;
  }

  ~suffix_tree ()
  {
    free_by_postorder (root);
  }
};

vector<string> ComputeSuffixTreeEdges (const string &text)
{
  vector<string> result;
  const int R = 5;
  suffix_tree<R> st (text);
  result = st.collect_nodes ();
  return result;
}

int main ()
{  
  std::ios_base::sync_with_stdio (false);

#if dbg
  cout << endl;
  ifstream cin ("input.txt");
  if (!cin)
    {
      cout << "Error: can`t find 'input.txt'." << endl;
      return -1;
    }
#endif

  string text;
  cin >> text;
  vector<string> edges;
  for (unsigned int i = 0; i < edges.size(); ++i)
    cout << edges[i] << endl;

#if dbg
  cin.close ();
  cout << endl;
#endif
  return 0;
}
