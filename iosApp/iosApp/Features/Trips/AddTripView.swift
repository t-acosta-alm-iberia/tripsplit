import SwiftUI
import SharedLogic

struct AddTripView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var viewModel: AddTripViewModel
    let onSaved: () -> Void

    init(repository: any TripRepository, onSaved: @escaping () -> Void) {
        _viewModel = State(initialValue: AddTripViewModel(repository: repository))
        self.onSaved = onSaved
    }

    var body: some View {
        NavigationStack {
            AddTripForm(
                name: $viewModel.name,
                participantNames: $viewModel.participantNames,
                isSaving: viewModel.isSaving,
                canSave: viewModel.canSave,
                errorMessage: viewModel.errorMessage,
                onCancel: { dismiss() },
                onSave: {
                    Task {
                        if await viewModel.save() {
                            onSaved()
                            dismiss()
                        }
                    }
                }
            )
        }
        .interactiveDismissDisabled(viewModel.isSaving)
    }
}

private struct AddTripForm: View {
    @Binding var name: String
    @Binding var participantNames: String
    let isSaving: Bool
    let canSave: Bool
    let errorMessage: String?
    let onCancel: () -> Void
    let onSave: () -> Void

    var body: some View {
        Form {
            Section("Trip") {
                TextField("Trip name", text: $name)
                    .textInputAutocapitalization(.words)
            }

            Section {
                Label("You", systemImage: "person.fill")
                TextField("Other participants", text: $participantNames, axis: .vertical)
                    .lineLimit(3...6)
                    .textInputAutocapitalization(.words)
                    .autocorrectionDisabled()
            } header: {
                Text("Participants")
            } footer: {
                Text("You are included automatically. Enter other names one per line, or leave this empty.")
            }

            if let errorMessage {
                Section {
                    Text(errorMessage)
                        .foregroundStyle(.red)
                }
            }
        }
        .disabled(isSaving)
        .navigationTitle("Add trip")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button("Cancel", action: onCancel)
                    .disabled(isSaving)
            }
            ToolbarItem(placement: .confirmationAction) {
                if isSaving {
                    ProgressView()
                        .accessibilityLabel("Saving trip")
                } else {
                    Button("Save", action: onSave)
                        .disabled(!canSave)
                }
            }
        }
    }
}

#Preview("Add trip") {
    NavigationStack {
        AddTripForm(
            name: .constant("Lisbon weekend"),
            participantNames: .constant("John\nDean"),
            isSaving: false,
            canSave: true,
            errorMessage: nil,
            onCancel: {},
            onSave: {}
        )
    }
}
