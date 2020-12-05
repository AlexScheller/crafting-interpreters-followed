namespace CSharpLox
{
    public interface IErrorReporter
    {
        public bool Errored { get; }
        public void Error(int line, int col, string message, string lineText, string type);
    }
}