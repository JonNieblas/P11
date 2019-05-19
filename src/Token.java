public class Token
{
    private String kind;
    private String details;

    public Token( String k, String d )
    {
        kind = k;  details = d;
        //System.out.println(this);
    }

    public boolean isKind( String s )
    {
        return kind.equals( s );
    }

    public String getKind()
    {  return kind;  }

    public String getDetails()
    { return details; }

    public String toString()
    {
        return "[" + kind + "," + details + "]";
    }

}